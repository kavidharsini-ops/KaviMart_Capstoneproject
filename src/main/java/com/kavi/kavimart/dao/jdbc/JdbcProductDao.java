package com.kavi.kavimart.dao.jdbc;

import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.model.PageResult;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.model.ProductQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JDBC implementation of marketplace product persistence. Listings are hidden, never deleted. */
public class JdbcProductDao implements ProductDao {
  private static final Logger LOG = LoggerFactory.getLogger(JdbcProductDao.class);

  private static final String SELECT_ACTIVE =
      "SELECT p.*, u.name AS seller_name FROM products p "
          + "JOIN users u ON u.id = p.seller_id WHERE p.is_active = TRUE";

  private final DataSource ds;

  /**
   * Create product persistence with the pooled data source.
   *
   * @param ds the connection pool
   */
  public JdbcProductDao(DataSource ds) {
    this.ds = ds;
  }

  /** {@inheritDoc} */
  @Override
  public PageResult<Product> search(ProductQuery q) {
    String filter = " AND (? = '' OR LOWER(p.name) LIKE ? "
        + "OR LOWER(COALESCE(p.description, '')) LIKE ?) AND (? = '' OR p.category = ?)";
    String order;
    switch (q.getSort()) {
      case "price_asc":
        order = " ORDER BY p.price ASC, p.id DESC";
        break;
      case "price_desc":
        order = " ORDER BY p.price DESC, p.id DESC";
        break;
      case "newest":
        order = " ORDER BY p.created_at DESC, p.id DESC";
        break;
      default:
        order = " ORDER BY p.name ASC";
        break;
    }
    String sql = SELECT_ACTIVE + filter + order + " LIMIT ? OFFSET ?";
    String countSql = "SELECT COUNT(*) FROM products p WHERE p.is_active = TRUE" + filter;
    String keyword = q.getKeyword().trim();
    String like = "%" + keyword.toLowerCase(Locale.ROOT) + "%";
    List<Product> items = new ArrayList<>();
    long total;
    try (Connection c = ds.getConnection();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, keyword);
      p.setString(2, like);
      p.setString(3, like);
      p.setString(4, q.getCategory());
      p.setString(5, q.getCategory());
      p.setInt(6, q.getPageSize());
      p.setInt(7, (q.getPage() - 1) * q.getPageSize());
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          items.add(map(r));
        }
      }
      try (PreparedStatement cp = c.prepareStatement(countSql)) {
        cp.setString(1, keyword);
        cp.setString(2, like);
        cp.setString(3, like);
        cp.setString(4, q.getCategory());
        cp.setString(5, q.getCategory());
        try (ResultSet cr = cp.executeQuery()) {
          cr.next();
          total = cr.getLong(1);
        }
      }
      return new PageResult<>(items, total, q.getPage(), q.getPageSize());
    } catch (SQLException e) {
      throw fail("search listings", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Product> findById(long id) {
    String sql = SELECT_ACTIVE + " AND p.id = ?";
    try (Connection c = ds.getConnection();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, id);
      try (ResultSet r = p.executeQuery()) {
        return r.next() ? Optional.of(map(r)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw fail("find listing", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public List<Product> findBySeller(long sellerId) {
    return list(SELECT_ACTIVE + " AND p.seller_id = ? ORDER BY p.created_at DESC", sellerId);
  }

  /** {@inheritDoc} */
  @Override
  public Product create(Product x) {
    String sql = "INSERT INTO products(seller_id, name, description, price, stock_qty, "
        + "category, image_url) VALUES(?, ?, ?, ?, ?, ?, ?)";
    try (Connection c = ds.getConnection();
        PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      p.setLong(1, x.getSellerId());
      p.setString(2, x.getName());
      p.setString(3, x.getDescription());
      p.setBigDecimal(4, x.getPrice());
      p.setInt(5, x.getStockQty());
      p.setString(6, x.getCategory());
      p.setString(7, x.getImageUrl());
      p.executeUpdate();
      try (ResultSet k = p.getGeneratedKeys()) {
        if (k.next()) {
          x.setId(k.getLong(1));
        }
      }
      return x;
    } catch (SQLException e) {
      throw fail("create listing", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean updateOwned(Product x, long sellerId) {
    String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_qty = ?, "
        + "category = ?, image_url = ? WHERE id = ? AND seller_id = ? AND is_active = TRUE";
    try (Connection c = ds.getConnection();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setString(1, x.getName());
      p.setString(2, x.getDescription());
      p.setBigDecimal(3, x.getPrice());
      p.setInt(4, x.getStockQty());
      p.setString(5, x.getCategory());
      p.setString(6, x.getImageUrl());
      p.setLong(7, x.getId());
      p.setLong(8, sellerId);
      return p.executeUpdate() > 0;
    } catch (SQLException e) {
      throw fail("update listing", e);
    }
  }

  /** {@inheritDoc} Hides the listing so existing orders keep their product reference. */
  @Override
  public boolean deleteOwned(long id, long sellerId) {
    String sql = "UPDATE products SET is_active = FALSE "
        + "WHERE id = ? AND seller_id = ? AND is_active = TRUE";
    try (Connection c = ds.getConnection();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, id);
      p.setLong(2, sellerId);
      return p.executeUpdate() > 0;
    } catch (SQLException e) {
      throw fail("hide listing", e);
    }
  }

  /** {@inheritDoc} Hides the listing so existing orders keep their product reference. */
  @Override
  public boolean deleteAny(long id) {
    String sql = "UPDATE products SET is_active = FALSE WHERE id = ? AND is_active = TRUE";
    try (Connection c = ds.getConnection();
        PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, id);
      return p.executeUpdate() > 0;
    } catch (SQLException e) {
      throw fail("hide listing", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public List<Product> findAll() {
    return list(SELECT_ACTIVE + " ORDER BY p.created_at DESC", null);
  }

  private List<Product> list(String sql, Long id) {
    List<Product> items = new ArrayList<>();
    try (Connection c = ds.getConnection();
        PreparedStatement p = c.prepareStatement(sql)) {
      if (id != null) {
        p.setLong(1, id);
      }
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          items.add(map(r));
        }
      }
      return items;
    } catch (SQLException e) {
      throw fail("list listings", e);
    }
  }

  private Product map(ResultSet r) throws SQLException {
    return new Product(
        r.getLong("id"),
        r.getLong("seller_id"),
        r.getString("seller_name"),
        r.getString("name"),
        r.getString("description"),
        r.getBigDecimal("price"),
        r.getInt("stock_qty"),
        r.getString("category"),
        r.getString("image_url"),
        r.getTimestamp("created_at").toLocalDateTime());
  }

  private RuntimeException fail(String action, SQLException e) {
    LOG.error("Database operation failed: {}", action, e);
    return new IllegalStateException("Database operation failed.", e);
  }
}