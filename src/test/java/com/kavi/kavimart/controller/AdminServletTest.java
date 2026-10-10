package com.kavi.kavimart.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.service.OrderService;
import com.kavi.kavimart.service.ProductService;
import com.kavi.kavimart.service.UserService;
import com.kavi.kavimart.util.SessionKeys;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminServletTest {

  private AdminServlet servlet;
  private ServletContext context;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private UserService userService;
  private OrderService orderService;
  private ProductService productService;
  private RequestDispatcher dispatcher;

  @BeforeEach
  void setUp() throws Exception {
    servlet = new AdminServlet();
    context = mock(ServletContext.class);
    ServletConfig config = mock(ServletConfig.class);
    when(config.getServletContext()).thenReturn(context);
    servlet.init(config);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    userService = mock(UserService.class);
    orderService = mock(OrderService.class);
    productService = mock(ProductService.class);
    dispatcher = mock(RequestDispatcher.class);

    when(context.getAttribute(SessionKeys.USER_SERVICE)).thenReturn(userService);
    when(context.getAttribute(SessionKeys.ORDER_SERVICE)).thenReturn(orderService);
    when(context.getAttribute(SessionKeys.PRODUCT_SERVICE)).thenReturn(productService);
    when(request.getContextPath()).thenReturn("");
  }

  @Test
  void adminDashboardLoadsAllData() throws Exception {
    when(userService.listUsers()).thenReturn(List.of());
    when(orderService.allOrders()).thenReturn(List.of());
    when(productService.listAll()).thenReturn(List.of());
    when(request.getRequestDispatcher("/WEB-INF/jsp/admin/index.jsp")).thenReturn(dispatcher);

    servlet.doGet(request, response);

    verify(request).setAttribute("users", List.of());
    verify(request).setAttribute("orders", List.of());
    verify(request).setAttribute("products", List.of());
    verify(dispatcher).forward(request, response);
  }

  @Test
  void adminCanBanUser() throws Exception {
    when(request.getParameter("action")).thenReturn("ban");
    when(request.getParameter("userId")).thenReturn("4");

    servlet.doPost(request, response);

    verify(userService).setActive(4L, false);
    verify(response).sendRedirect("/admin");
  }

  @Test
  void adminCanActivateUser() throws Exception {
    when(request.getParameter("action")).thenReturn("activate");
    when(request.getParameter("userId")).thenReturn("4");

    servlet.doPost(request, response);

    verify(userService).setActive(4L, true);
    verify(response).sendRedirect("/admin");
  }

  @Test
  void adminCanRemoveListing() throws Exception {
    when(request.getParameter("action")).thenReturn("remove-listing");
    when(request.getParameter("productId")).thenReturn("101");

    servlet.doPost(request, response);

    verify(productService).adminDelete(101L);
    verify(response).sendRedirect("/admin");
  }
}
