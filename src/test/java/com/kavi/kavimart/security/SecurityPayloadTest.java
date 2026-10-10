package com.kavi.kavimart.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.dao.ReviewDao;
import com.kavi.kavimart.dao.UserDao;
import com.kavi.kavimart.dao.jdbc.JdbcDatabaseDao;
import com.kavi.kavimart.dao.jdbc.JdbcProductDao;
import com.kavi.kavimart.dao.jdbc.JdbcReviewDao;
import com.kavi.kavimart.dao.jdbc.JdbcUserDao;
import com.kavi.kavimart.exception.AppException;
import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.model.PageResult;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.model.ProductQuery;
import com.kavi.kavimart.model.Review;
import com.kavi.kavimart.service.ProductService;
import com.kavi.kavimart.service.ReviewService;
import com.kavi.kavimart.service.UserService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityPayloadTest {

  private JdbcDataSource dataSource;
  private UserDao userDao;
  private ProductDao productDao;
  private ReviewDao reviewDao;
  private UserService userService;
  private ProductService productService;
  private ReviewService reviewService;

  @BeforeEach
  void setUp() {
    dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:mem:securitytest_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
    dataSource.setUser("sa");
    new JdbcDatabaseDao(dataSource).initialize();

    userDao = new JdbcUserDao(dataSource);
    productDao = new JdbcProductDao(dataSource);
    reviewDao = new JdbcReviewDao(dataSource);

    userService = new UserService(userDao);
    productService = new ProductService(productDao);
    reviewService = new ReviewService(reviewDao, productDao);
  }

  @Test
  void sqlInjectionInLoginEmailIsSafelyTreatedAsLiteral() {
    String sqlPayload = "' OR '1'='1' --";
    // 1. Direct DAO query safely handles injection via PreparedStatement
    Optional<?> userOpt = userDao.findByEmail(sqlPayload);
    assertTrue(userOpt.isEmpty(), "SQL injection payload in email should not match any user");

    // 2. Service level email validator blocks malformed email before database call
    ValidationException valEx = assertThrows(ValidationException.class, () ->
        userService.login(sqlPayload, "password123"));
    assertEquals(400, valEx.getStatus(), "Malformed injection payload fails email format validation");

    // 3. Syntactically valid email format with injection attempt fails authentication
    AppException authEx = assertThrows(AppException.class, () ->
        userService.login("injected@example.com", "password123"));
    assertEquals(401, authEx.getStatus(), "Non-existent user authentication returns 401 UNAUTHORIZED");
  }

  @Test
  void sqlInjectionInCatalogSearchIsSafelyHandledByPreparedStatement() {
    String attackQuery1 = "' OR 1=1 --";
    PageResult<Product> result1 = productDao.search(new ProductQuery(attackQuery1, "", "price_asc", 1, 10));
    assertEquals(0, result1.getTotal(), "SQL injection should find 0 matches rather than dumping table");

    String attackQuery2 = "'; DROP TABLE products; --";
    assertDoesNotThrow(() -> {
      PageResult<Product> result2 = productDao.search(new ProductQuery(attackQuery2, "", "price_asc", 1, 10));
      assertEquals(0, result2.getTotal());
    });

    // Verify products table was not dropped
    assertFalse(productDao.findAll().isEmpty(), "Products table must remain intact after SQL injection query");
  }

  @Test
  void xssPayloadInProductNameStoredAsSafeLiteralString() {
    String xssName = "<script>alert('XSS')</script>";
    Product p = new Product();
    p.setName(xssName);
    p.setDescription("Test description <img src=x onerror=alert(1)>");
    p.setPrice(new BigDecimal("199.00"));
    p.setStockQty(5);
    p.setCategory("Apparel");
    p.setImageUrl("https://example.com/item.jpg");

    Product created = productService.create(2L, p);
    assertNotNull(created);
    assertTrue(created.getId() > 0);

    Product retrieved = productService.get(created.getId());
    assertEquals(xssName, retrieved.getName(), "XSS payload is stored as literal characters without code execution");
  }

  @Test
  void xssPayloadInReviewCommentStoredSafelyWithoutExecution() throws Exception {
    // Seed a DELIVERED order for buyer 4 on product 101 to satisfy verified-purchase check
    try (Connection conn = dataSource.getConnection()) {
      long orderId;
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO orders(buyer_id, status, total_amount) VALUES(4, 'DELIVERED', 899.00)",
          Statement.RETURN_GENERATED_KEYS)) {
        ps.executeUpdate();
        try (var keys = ps.getGeneratedKeys()) {
          keys.next();
          orderId = keys.getLong(1);
        }
      }
      try (PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO order_items(order_id, product_id, quantity, unit_price) VALUES(?, 101, 1, 899.00)")) {
        ps.setLong(1, orderId);
        ps.executeUpdate();
      }
    }

    String xssComment = "<svg/onload=alert('XSS')>";
    Review review = reviewService.create(4L, 101L, 5, xssComment);
    assertNotNull(review);
    assertEquals(xssComment, review.getComment(), "XSS comment is stored as literal string");

    // Also test direct DAO storage of XSS review
    Review daoReview = reviewDao.create(4L, 102L, 4, "<img src=x onerror=alert('hack')>");
    assertEquals("<img src=x onerror=alert('hack')>", daoReview.getComment());
  }
}
