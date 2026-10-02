package com.kavi.kavimart.listener;

import com.kavi.kavimart.dao.jdbc.JdbcCartDao;
import com.kavi.kavimart.dao.jdbc.JdbcDatabaseDao;
import com.kavi.kavimart.dao.jdbc.JdbcOrderDao;
import com.kavi.kavimart.dao.jdbc.JdbcProductDao;
import com.kavi.kavimart.dao.jdbc.JdbcReviewDao;
import com.kavi.kavimart.dao.jdbc.JdbcUserDao;
import com.kavi.kavimart.filter.AuthFilter;
import com.kavi.kavimart.controller.AdminServlet;
import com.kavi.kavimart.controller.ApiServlet;
import com.kavi.kavimart.controller.AuthServlet;
import com.kavi.kavimart.controller.CartServlet;
import com.kavi.kavimart.controller.HomeServlet;
import com.kavi.kavimart.controller.OrderServlet;
import com.kavi.kavimart.controller.ProductServlet;
import com.kavi.kavimart.controller.ReviewServlet;
import com.kavi.kavimart.filter.EncodingFilter;
import com.kavi.kavimart.filter.LoggingFilter;
import com.kavi.kavimart.service.CartService;
import com.kavi.kavimart.service.OrderService;
import com.kavi.kavimart.service.ProductService;
import com.kavi.kavimart.service.ReviewService;
import com.kavi.kavimart.service.UserService;
import com.kavi.kavimart.service.payment.PaymentStrategyFactory;
import com.kavi.kavimart.util.SessionKeys;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Properties;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Creates the pooled data source, initializes schema/data, and wires application services. */
@WebListener
public class AppContextListener implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(AppContextListener.class);
  private HikariDataSource dataSource;

  /** {@inheritDoc} */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    ServletContext context = event.getServletContext();
    registerServlet(context, "home", new HomeServlet(), "");
    registerServlet(context, "auth", new AuthServlet(), "/auth/*");
    registerServlet(context, "products", new ProductServlet(), "/catalog", "/products/*",
        "/seller/products/*");
    registerServlet(context, "cart", new CartServlet(), "/buyer/cart");
    registerServlet(context, "orders", new OrderServlet(), "/buyer/orders", "/seller/orders",
        "/seller/dashboard");
    registerServlet(context, "reviews", new ReviewServlet(), "/reviews/*");
    registerServlet(context, "admin", new AdminServlet(), "/admin/*");
    registerServlet(context, "api", new ApiServlet(), "/api/v1/*");
    context.setSessionTimeout(30);
    registerFilter(context, "encodingFilter", new EncodingFilter());
    registerFilter(context, "loggingFilter", new LoggingFilter());
    registerFilter(context, "authFilter", new AuthFilter());
    try {
      Properties props = new Properties();
      try (InputStream in = getClass().getResourceAsStream("/config.properties")) {
        if (in != null) {
          props.load(in);
        }
      }
      String url = System.getenv().getOrDefault("JDBC_URL", props.getProperty("jdbc.url",
          "jdbc:h2:file:./data/kavimart;AUTO_SERVER=TRUE"));
      String username = System.getenv().getOrDefault("JDBC_USER",
          props.getProperty("jdbc.username", "sa"));
      String password = System.getenv().getOrDefault("JDBC_PASSWORD",
          props.getProperty("jdbc.password", ""));
      if (url.startsWith("jdbc:h2:file:")) {
        Files.createDirectories(Path.of("data"));
      }

      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(url);
      config.setUsername(username);
      config.setPassword(password);
      config.setDriverClassName("org.h2.Driver");
      config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikari.maximumPoolSize", "10")));
      config.setPoolName("KaviMartPool");
      dataSource = new HikariDataSource(config);
      new JdbcDatabaseDao(dataSource).initialize();

      JdbcUserDao users = new JdbcUserDao(dataSource);
      JdbcProductDao products = new JdbcProductDao(dataSource);
      JdbcCartDao carts = new JdbcCartDao(dataSource);
      JdbcOrderDao orders = new JdbcOrderDao(dataSource);
      JdbcReviewDao reviews = new JdbcReviewDao(dataSource);
      CartService cartService = new CartService(carts, products);
      context.setAttribute(SessionKeys.DATA_SOURCE, dataSource);
      context.setAttribute(SessionKeys.USER_SERVICE, new UserService(users));
      context.setAttribute(SessionKeys.PRODUCT_SERVICE, new ProductService(products));
      context.setAttribute(SessionKeys.CART_SERVICE, cartService);
      context.setAttribute(SessionKeys.ORDER_SERVICE,
          new OrderService(orders, cartService, PaymentStrategyFactory.create()));
      context.setAttribute(SessionKeys.REVIEW_SERVICE, new ReviewService(reviews, products));
      LOG.info("KaviMart initialized with H2 database {}",
          url.startsWith("jdbc:h2:file:") ? "file database" : "memory database");
    } catch (Exception e) {
      LOG.error("KaviMart startup failed", e);
      throw new IllegalStateException("KaviMart could not start.", e);
    }
  }

  private void registerServlet(ServletContext context, String name, HttpServlet servlet,
      String... mappings) {
    ServletRegistration.Dynamic registration = context.addServlet(name, servlet);
    if (registration == null) {
      throw new IllegalStateException("Servlet is already registered: " + name);
    }
    registration.addMapping(mappings);
    registration.setLoadOnStartup(1);
  }

  private void registerFilter(ServletContext context, String name, Filter filter) {
    FilterRegistration.Dynamic registration = context.addFilter(name, filter);
    registration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }

  /** {@inheritDoc} */
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    if (dataSource != null && !dataSource.isClosed()) {
      dataSource.close();
    }
  }
}
