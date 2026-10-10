package com.kavi.kavimart.dao.jdbc;

import com.kavi.kavimart.dao.OrderDao;
import com.kavi.kavimart.dto.OrderLineDTO;
import com.kavi.kavimart.dto.OrderSummaryDTO;
import com.kavi.kavimart.exception.ConflictException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC implementation of transactional order persistence.
 * Implements atomic checkout with stock reservation and cart cleanup.
 */
public class JdbcOrderDao implements OrderDao {
  private static final Logger LOG = LoggerFactory.getLogger(JdbcOrderDao.class);
  private final DataSource ds;

  /**
   * Constructs the order DAO with the application DataSource.
   *
   * @param ds the HikariCP DataSource
   */
  public JdbcOrderDao(DataSource ds) {
    this.ds = ds;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public OrderSummaryDTO placeOrder(long buyerId) {
    String cartSql = "SELECT c.product_id, c.quantity, p.name, p.price, p.stock_qty, p.seller_id "
        + "FROM cart_items c "
        + "JOIN products p ON p.id = c.product_id "
        + "WHERE c.user_id = ? FOR UPDATE";

    try (Connection c = ds.getConnection()) {
      c.setAutoCommit(false);
      try {
        List<PurchaseLine> lines = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement(cartSql)) {
          p.setLong(1, buyerId);
          try (ResultSet r = p.executeQuery()) {
            while (r.next()) {
              lines.add(new PurchaseLine(
                  r.getLong("product_id"),
                  r.getString("name"),
                  r.getInt("quantity"),
                  r.getBigDecimal("price"),
                  r.getLong("seller_id")
              ));
            }
          }
        }

        if (lines.isEmpty()) {
          throw new ConflictException("Your cart is empty.");
        }

        BigDecimal total = lines.stream()
            .map(x -> x.price.multiply(BigDecimal.valueOf(x.quantity)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long orderId;
        String insertOrder = "INSERT INTO orders(buyer_id, status, total_amount) VALUES(?, 'PENDING', ?)";
        try (PreparedStatement p = c.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
          p.setLong(1, buyerId);
          p.setBigDecimal(2, total);
          p.executeUpdate();
          try (ResultSet k = p.getGeneratedKeys()) {
            k.next();
            orderId = k.getLong(1);
          }
        }

        for (PurchaseLine line : lines) {
          String updateStock = "UPDATE products SET stock_qty = stock_qty - ? "
              + "WHERE id = ? AND stock_qty >= ?";
          try (PreparedStatement stock = c.prepareStatement(updateStock)) {
            stock.setInt(1, line.quantity);
            stock.setLong(2, line.productId);
            stock.setInt(3, line.quantity);
            if (stock.executeUpdate() != 1) {
              throw new ConflictException("A product in your cart no longer has enough stock.");
            }
          }

          String insertItem = "INSERT INTO order_items(order_id, product_id, quantity, unit_price) "
              + "VALUES(?, ?, ?, ?)";
          try (PreparedStatement item = c.prepareStatement(insertItem)) {
            item.setLong(1, orderId);
            item.setLong(2, line.productId);
            item.setInt(3, line.quantity);
            item.setBigDecimal(4, line.price);
            item.executeUpdate();
          }
        }

        try (PreparedStatement clear = c.prepareStatement("DELETE FROM cart_items WHERE user_id = ?")) {
          clear.setLong(1, buyerId);
          clear.executeUpdate();
        }

        c.commit();

        List<OrderLineDTO> result = lines.stream()
            .map(x -> new OrderLineDTO(x.productId, x.name, x.quantity, x.price, x.sellerId))
            .toList();

        return OrderSummaryDTO.builder()
            .id(orderId)
            .buyerId(buyerId)
            .status("PENDING")
            .totalAmount(total)
            .createdAt(LocalDateTime.now())
            .items(result)
            .build();
      } catch (RuntimeException | SQLException e) {
        c.rollback();
        if (e instanceof RuntimeException re) {
          throw re;
        }
        throw fail("checkout", (SQLException) e);
      } finally {
        c.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw fail("checkout", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<OrderSummaryDTO> findById(long id) {
    List<OrderSummaryDTO> found = queryOrders(" WHERE o.id = ?", id, null);
    return found.stream().findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<OrderSummaryDTO> findByBuyer(long id) {
    return queryOrders(" WHERE o.buyer_id = ?", id, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<OrderSummaryDTO> findBySeller(long id) {
    String clause = " WHERE EXISTS ("
        + "SELECT 1 FROM order_items i "
        + "JOIN products p ON p.id = i.product_id "
        + "WHERE i.order_id = o.id AND p.seller_id = ?)";
    return queryOrders(clause, id, id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<OrderSummaryDTO> findAll() {
    return queryOrders("", null, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean transitionSeller(long id, long seller, String expected, String next) {
    String sql = "UPDATE orders SET status = ? "
        + "WHERE id = ? AND status = ? AND EXISTS ("
        + "SELECT 1 FROM order_items i "
        + "JOIN products p ON p.id = i.product_id "
        + "WHERE i.order_id = orders.id AND p.seller_id = ?)";
    try (Connection c = ds.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, next);
      p.setLong(2, id);
      p.setString(3, expected);
      p.setLong(4, seller);
      return p.executeUpdate() > 0;
    } catch (SQLException e) {
      throw fail("change order status", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean cancelPending(long orderId, long buyerId) {
    try (Connection c = ds.getConnection()) {
      c.setAutoCommit(false);
      try {
        List<long[]> rows = new ArrayList<>();
        try (PreparedStatement p = c.prepareStatement("SELECT product_id, quantity FROM order_items WHERE order_id = ?")) {
          p.setLong(1, orderId);
          try (ResultSet r = p.executeQuery()) {
            while (r.next()) {
              rows.add(new long[]{r.getLong(1), r.getLong(2)});
            }
          }
        }

        String cancelSql = "UPDATE orders SET status = 'CANCELLED' "
            + "WHERE id = ? AND buyer_id = ? AND status = 'PENDING'";
        try (PreparedStatement p = c.prepareStatement(cancelSql)) {
          p.setLong(1, orderId);
          p.setLong(2, buyerId);
          if (p.executeUpdate() != 1) {
            c.rollback();
            return false;
          }
        }

        for (long[] row : rows) {
          try (PreparedStatement p = c.prepareStatement("UPDATE products SET stock_qty = stock_qty + ? WHERE id = ?")) {
            p.setLong(1, row[1]);
            p.setLong(2, row[0]);
            p.executeUpdate();
          }
        }

        c.commit();
        return true;
      } catch (SQLException e) {
        c.rollback();
        throw fail("cancel order", e);
      } finally {
        c.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw fail("cancel order", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, BigDecimal> sellerStats(long sellerId) {
    String sql = "SELECT COUNT(DISTINCT o.id) AS order_count, "
        + "COALESCE(SUM(i.unit_price * i.quantity), 0) AS revenue "
        + "FROM orders o "
        + "JOIN order_items i ON i.order_id = o.id "
        + "JOIN products p ON p.id = i.product_id "
        + "WHERE p.seller_id = ? AND o.status <> 'CANCELLED'";
    try (Connection c = ds.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, sellerId);
      try (ResultSet r = p.executeQuery()) {
        r.next();
        Map<String, BigDecimal> m = new HashMap<>();
        m.put("orders", BigDecimal.valueOf(r.getLong("order_count")));
        m.put("revenue", r.getBigDecimal("revenue"));
        return m;
      }
    } catch (SQLException e) {
      throw fail("seller statistics", e);
    }
  }

  private List<OrderSummaryDTO> queryOrders(String clause, Long arg, Long sellerFilter) {
    String sql = "SELECT o.id, o.buyer_id, o.status, o.total_amount, o.created_at, u.name AS buyer_name "
        + "FROM orders o "
        + "JOIN users u ON u.id = o.buyer_id"
        + clause
        + " ORDER BY o.created_at DESC";
    List<OrderSummaryDTO> out = new ArrayList<>();
    try (Connection c = ds.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
      if (arg != null) {
        p.setLong(1, arg);
      }
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          long id = r.getLong("id");
          out.add(OrderSummaryDTO.builder()
              .id(id)
              .buyerId(r.getLong("buyer_id"))
              .buyerName(r.getString("buyer_name"))
              .status(r.getString("status"))
              .totalAmount(r.getBigDecimal("total_amount"))
              .createdAt(r.getTimestamp("created_at").toLocalDateTime())
              .items(loadItems(c, id, sellerFilter))
              .build());
        }
      }
      return out;
    } catch (SQLException e) {
      throw fail("read orders", e);
    }
  }

  private List<OrderLineDTO> loadItems(Connection c, long orderId, Long seller) throws SQLException {
    String sql = "SELECT i.product_id, i.quantity, i.unit_price, p.name, p.seller_id "
        + "FROM order_items i "
        + "JOIN products p ON p.id = i.product_id "
        + "WHERE i.order_id = ?"
        + (seller == null ? "" : " AND p.seller_id = ?");
    List<OrderLineDTO> items = new ArrayList<>();
    try (PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, orderId);
      if (seller != null) {
        p.setLong(2, seller);
      }
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          items.add(new OrderLineDTO(
              r.getLong("product_id"),
              r.getString("name"),
              r.getInt("quantity"),
              r.getBigDecimal("unit_price"),
              r.getLong("seller_id")
          ));
        }
      }
    }
    return items;
  }

  private RuntimeException fail(String action, SQLException e) {
    LOG.error("Database operation failed: {}", action, e);
    return new IllegalStateException("Database operation failed.", e);
  }

  private static final class PurchaseLine {
    final long productId;
    final long sellerId;
    final String name;
    final int quantity;
    final BigDecimal price;

    PurchaseLine(long p, String n, int q, BigDecimal price, long seller) {
      this.productId = p;
      this.name = n;
      this.quantity = q;
      this.price = price;
      this.sellerId = seller;
    }
  }
}
