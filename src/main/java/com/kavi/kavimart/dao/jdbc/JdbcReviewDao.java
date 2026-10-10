package com.kavi.kavimart.dao.jdbc;

import com.kavi.kavimart.dao.ReviewDao;
import com.kavi.kavimart.exception.ConflictException;
import com.kavi.kavimart.model.Review;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC implementation of verified product review persistence.
 */
public class JdbcReviewDao implements ReviewDao {
  private static final Logger LOG = LoggerFactory.getLogger(JdbcReviewDao.class);
  private final DataSource ds;

  /**
   * Initializes the review persistence with a pooled data source.
   *
   * @param ds the HikariCP DataSource
   */
  public JdbcReviewDao(DataSource ds) {
    this.ds = ds;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Review create(long user, long product, int rating, String comment) {
    String sql = "INSERT INTO reviews(user_id, product_id, rating, comment) VALUES(?, ?, ?, ?)";
    try (Connection c = ds.getConnection();
         PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      p.setLong(1, user);
      p.setLong(2, product);
      p.setInt(3, rating);
      p.setString(4, comment);
      p.executeUpdate();
      try (ResultSet k = p.getGeneratedKeys()) {
        k.next();
        return new Review(k.getLong(1), product, user, "", rating, comment, LocalDateTime.now());
      }
    } catch (SQLException e) {
      if ("23505".equals(e.getSQLState())) {
        throw new ConflictException("You have already reviewed this product.");
      }
      throw fail("create review", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Review> findByProduct(long id) {
    String sql = "SELECT r.id, r.product_id, r.user_id, r.rating, r.comment, r.created_at, "
        + "u.name AS user_name "
        + "FROM reviews r "
        + "JOIN users u ON u.id = r.user_id "
        + "WHERE r.product_id = ? "
        + "ORDER BY r.created_at DESC";
    List<Review> items = new ArrayList<>();
    try (Connection c = ds.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, id);
      try (ResultSet r = p.executeQuery()) {
        while (r.next()) {
          items.add(new Review(
              r.getLong("id"),
              r.getLong("product_id"),
              r.getLong("user_id"),
              r.getString("user_name"),
              r.getInt("rating"),
              r.getString("comment"),
              r.getTimestamp("created_at").toLocalDateTime()
          ));
        }
      }
      return items;
    } catch (SQLException e) {
      throw fail("list reviews", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double averageRating(long id) {
    String sql = "SELECT COALESCE(AVG(rating), 0) FROM reviews WHERE product_id = ?";
    try (Connection c = ds.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, id);
      try (ResultSet r = p.executeQuery()) {
        r.next();
        return r.getDouble(1);
      }
    } catch (SQLException e) {
      throw fail("average reviews", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasDeliveredPurchase(long user, long product) {
    String sql = "SELECT COUNT(*) FROM orders o "
        + "JOIN order_items i ON i.order_id = o.id "
        + "WHERE o.buyer_id = ? AND o.status = 'DELIVERED' AND i.product_id = ?";
    try (Connection c = ds.getConnection();
         PreparedStatement p = c.prepareStatement(sql)) {
      p.setLong(1, user);
      p.setLong(2, product);
      try (ResultSet r = p.executeQuery()) {
        r.next();
        return r.getLong(1) > 0;
      }
    } catch (SQLException e) {
      throw fail("check purchase", e);
    }
  }

  private RuntimeException fail(String action, SQLException e) {
    LOG.error("Database operation failed: {}", action, e);
    return new IllegalStateException("Database operation failed.", e);
  }
}
