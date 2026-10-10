package com.kavi.kavimart.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.service.OrderService;
import com.kavi.kavimart.util.SessionKeys;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderServletTest {

  private OrderServlet servlet;
  private ServletContext context;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private HttpSession session;
  private OrderService orderService;
  private RequestDispatcher dispatcher;

  @BeforeEach
  void setUp() throws Exception {
    servlet = new OrderServlet();
    context = mock(ServletContext.class);
    ServletConfig config = mock(ServletConfig.class);
    when(config.getServletContext()).thenReturn(context);
    servlet.init(config);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    session = mock(HttpSession.class);
    orderService = mock(OrderService.class);
    dispatcher = mock(RequestDispatcher.class);

    when(context.getAttribute(SessionKeys.ORDER_SERVICE)).thenReturn(orderService);
    when(request.getSession(false)).thenReturn(session);
    when(request.getContextPath()).thenReturn("");
  }

  @Test
  void buyerLoadsOrderHistory() throws Exception {
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(4L, "Buyer", "buyer@example.com", "BUYER", true));
    when(request.getServletPath()).thenReturn("/buyer/orders");
    when(orderService.buyerOrders(4L)).thenReturn(List.of());
    when(request.getRequestDispatcher("/WEB-INF/jsp/buyer/orders.jsp")).thenReturn(dispatcher);

    servlet.doGet(request, response);

    verify(request).setAttribute("orders", List.of());
    verify(dispatcher).forward(request, response);
  }

  @Test
  void sellerLoadsDashboard() throws Exception {
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(2L, "Seller", "seller@example.com", "SELLER", true));
    when(request.getServletPath()).thenReturn("/seller/dashboard");
    when(orderService.sellerOrders(2L)).thenReturn(List.of());
    when(orderService.sellerStats(2L)).thenReturn(Map.of("orders", BigDecimal.ZERO, "revenue", BigDecimal.ZERO));
    when(request.getRequestDispatcher("/WEB-INF/jsp/seller/dashboard.jsp")).thenReturn(dispatcher);

    servlet.doGet(request, response);

    verify(request).setAttribute("orders", List.of());
    verify(request).setAttribute("stats", Map.of("orders", BigDecimal.ZERO, "revenue", BigDecimal.ZERO));
    verify(dispatcher).forward(request, response);
  }

  @Test
  void buyerPlaceOrderCheckoutFlow() throws Exception {
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(4L, "Buyer", "buyer@example.com", "BUYER", true));
    when(request.getServletPath()).thenReturn("/buyer/orders");
    when(request.getParameter("action")).thenReturn(null);

    servlet.doPost(request, response);

    verify(orderService).placeOrder(4L);
    verify(response).sendRedirect("/buyer/orders?placed=1");
  }

  @Test
  void buyerCancelPendingOrder() throws Exception {
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(4L, "Buyer", "buyer@example.com", "BUYER", true));
    when(request.getServletPath()).thenReturn("/buyer/orders");
    when(request.getParameter("action")).thenReturn("cancel");
    when(request.getParameter("orderId")).thenReturn("5");

    servlet.doPost(request, response);

    verify(orderService).cancel(4L, 5L);
    verify(response).sendRedirect("/buyer/orders");
  }

  @Test
  void sellerAdvancesOrderStatus() throws Exception {
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(2L, "Seller", "seller@example.com", "SELLER", true));
    when(request.getServletPath()).thenReturn("/seller/orders");
    when(request.getParameter("orderId")).thenReturn("8");
    when(request.getParameter("status")).thenReturn("CONFIRMED");

    servlet.doPost(request, response);

    verify(orderService).updateSellerStatus(2L, 8L, "CONFIRMED");
    verify(response).sendRedirect("/seller/orders");
  }
}
