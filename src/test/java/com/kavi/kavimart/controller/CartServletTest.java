package com.kavi.kavimart.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.service.CartService;
import com.kavi.kavimart.util.SessionKeys;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

class CartServletTest {

  @Test
  void cartPageLoadsOnlyTheSignedInBuyersCart() throws Exception {
    ServletContext context = mock(ServletContext.class);
    CartService carts = mock(CartService.class);
    when(context.getAttribute(SessionKeys.CART_SERVICE)).thenReturn(carts);

    ServletConfig config = mock(ServletConfig.class);
    when(config.getServletContext()).thenReturn(context);

    CartServlet servlet = new CartServlet();
    servlet.init(config);

    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse res = mock(HttpServletResponse.class);
    HttpSession session = mock(HttpSession.class);
    RequestDispatcher dispatcher = mock(RequestDispatcher.class);

    when(req.getSession(false)).thenReturn(session);
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(4, "Buyer", "buyer@example.com", "BUYER", true));
    when(carts.getItems(4)).thenReturn(List.of());
    when(carts.total(4)).thenReturn(BigDecimal.ZERO);
    when(req.getRequestDispatcher("/WEB-INF/jsp/buyer/cart.jsp")).thenReturn(dispatcher);

    servlet.doGet(req, res);

    verify(req).setAttribute("items", List.of());
    verify(req).setAttribute("total", BigDecimal.ZERO);
    verify(dispatcher).forward(req, res);
  }
}
