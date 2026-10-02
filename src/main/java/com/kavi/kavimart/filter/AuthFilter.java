package com.kavi.kavimart.filter;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.util.JsonUtil;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Protects authenticated routes and enforces buyer, seller, and admin roles. */
public class AuthFilter implements Filter {

  /** {@inheritDoc} */
  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String path = request.getRequestURI().substring(request.getContextPath().length());
    String method = request.getMethod();
    if (isPublic(path, method)) {
      chain.doFilter(req, res);
      return;
    }
    HttpSession session = request.getSession(false);
    Object value = session == null ? null : session.getAttribute(SessionKeys.USER);
    if (!(value instanceof UserResponseDTO user)) {
      deny(request, response, 401, "UNAUTHORIZED", "Please sign in to continue.");
      return;
    }
    String required = requiredRole(path, method);
    if (required != null && !required.equals(user.getRole())) {
      deny(request, response, 403, "FORBIDDEN",
          "You do not have permission to access this page.");
      return;
    }
    chain.doFilter(req, res);
  }

  private boolean isPublic(String path, String method) {
    if (path.equals("/") || path.equals("/catalog") || path.equals("/auth/login")
        || path.equals("/auth/register") || path.startsWith("/assets/")
        || path.equals("/favicon.ico")) {
      return true;
    }
    if (path.startsWith("/products/")) {
      return true;
    }
    if ("POST".equals(method) && path.equals("/api/v1/chat")) {
      return true;
    }
    return "GET".equals(method) && (path.equals("/api/v1/health")
        || path.startsWith("/api/v1/products") || path.equals("/api/v1/reviews"));
  }

  private String requiredRole(String path, String method) {
    if (path.equals("/admin") || path.startsWith("/admin/")) {
      return "ADMIN";
    }
    if (path.startsWith("/seller/") || path.startsWith("/api/v1/seller/")) {
      return "SELLER";
    }
    if (path.startsWith("/buyer/") || path.startsWith("/api/v1/cart")
        || path.startsWith("/api/v1/orders")
        || (path.startsWith("/api/v1/reviews") && "POST".equals(method))) {
      return "BUYER";
    }
    return null;
  }

  private void deny(HttpServletRequest request, HttpServletResponse response, int status,
      String code, String message) throws IOException, ServletException {
    if (request.getRequestURI().contains("/api/")) {
      JsonUtil.error(response, status, code, message, null);
      return;
    }
    if (status == 401) {
      response.sendRedirect(request.getContextPath() + "/auth/login?next="
          + URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8));
      return;
    }
    response.setStatus(status);
    request.setAttribute("errorMessage", message);
    request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
  }
}