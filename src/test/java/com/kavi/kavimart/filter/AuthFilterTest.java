package com.kavi.kavimart.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.util.SessionKeys;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthFilterTest {

  private AuthFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain chain;
  private HttpSession session;
  private RequestDispatcher dispatcher;

  @BeforeEach
  void setUp() {
    filter = new AuthFilter();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    chain = mock(FilterChain.class);
    session = mock(HttpSession.class);
    dispatcher = mock(RequestDispatcher.class);

    when(request.getContextPath()).thenReturn("");
    when(request.getRequestDispatcher("/WEB-INF/jsp/error.jsp")).thenReturn(dispatcher);
  }

  @Test
  void publicRoutesPassWithoutAuthentication() throws Exception {
    when(request.getRequestURI()).thenReturn("/catalog");
    when(request.getMethod()).thenReturn("GET");

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
    verify(response, never()).sendRedirect(any());
  }

  @Test
  void unauthenticatedWebRouteRedirectsToLogin() throws Exception {
    when(request.getRequestURI()).thenReturn("/buyer/orders");
    when(request.getMethod()).thenReturn("GET");
    when(request.getSession(false)).thenReturn(null);

    filter.doFilter(request, response, chain);

    verify(chain, never()).doFilter(request, response);
    verify(response).sendRedirect("/auth/login?next=%2Fbuyer%2Forders");
  }

  @Test
  void unauthenticatedApiRouteReturns401Json() throws Exception {
    when(request.getRequestURI()).thenReturn("/api/v1/cart");
    when(request.getMethod()).thenReturn("POST");
    when(request.getSession(false)).thenReturn(null);

    StringWriter writer = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(writer));

    filter.doFilter(request, response, chain);

    verify(chain, never()).doFilter(request, response);
    verify(response).setStatus(401);
  }

  @Test
  void buyerAccessingAdminPageReturns403Forbidden() throws Exception {
    when(request.getRequestURI()).thenReturn("/admin");
    when(request.getMethod()).thenReturn("GET");
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(4, "Buyer", "buyer@example.com", "BUYER", true));

    filter.doFilter(request, response, chain);

    verify(chain, never()).doFilter(request, response);
    verify(response).setStatus(403);
    verify(dispatcher).forward(request, response);
  }

  @Test
  void buyerAccessingSellerPageReturns403Forbidden() throws Exception {
    when(request.getRequestURI()).thenReturn("/seller/products");
    when(request.getMethod()).thenReturn("GET");
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(4, "Buyer", "buyer@example.com", "BUYER", true));

    filter.doFilter(request, response, chain);

    verify(chain, never()).doFilter(request, response);
    verify(response).setStatus(403);
    verify(dispatcher).forward(request, response);
  }

  @Test
  void sellerAccessingSellerPageAllowed() throws Exception {
    when(request.getRequestURI()).thenReturn("/seller/products");
    when(request.getMethod()).thenReturn("GET");
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(2, "Seller", "seller@example.com", "SELLER", true));

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
  }

  @Test
  void adminAccessingAdminPageAllowed() throws Exception {
    when(request.getRequestURI()).thenReturn("/admin");
    when(request.getMethod()).thenReturn("GET");
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(SessionKeys.USER)).thenReturn(
        new UserResponseDTO(1, "Admin", "admin@kavimart.com", "ADMIN", true));

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
  }
}
