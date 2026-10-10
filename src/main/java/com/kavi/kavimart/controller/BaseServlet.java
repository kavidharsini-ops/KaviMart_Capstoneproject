package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.exception.AppException;
import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.util.JsonUtil;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base servlet providing shared convenience helpers for session retrieval, service lookup, and error handling.
 */
public abstract class BaseServlet extends HttpServlet {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected UserResponseDTO user(HttpServletRequest r) {
    HttpSession session = r.getSession(false);
    Object u = session == null ? null : session.getAttribute(SessionKeys.USER);
    return u instanceof UserResponseDTO dto ? dto : null;
  }

  protected <T> T service(ServletContext c, String key, Class<T> type) {
    return type.cast(c.getAttribute(key));
  }

  protected void error(HttpServletRequest req, HttpServletResponse res, Throwable t)
      throws IOException, ServletException {
    if (t instanceof AppException e) {
      if (req.getRequestURI().contains("/api/")) {
        Map<String, String> fields = t instanceof ValidationException v ? v.getFields() : null;
        JsonUtil.error(res, e.getStatus(), e.getCode(), e.getMessage(), fields);
        return;
      }
      res.setStatus(e.getStatus());
      req.setAttribute("errorMessage", e.getMessage());
      req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, res);
      return;
    }

    log.error("Request failed", t);
    if (req.getRequestURI().contains("/api/")) {
      JsonUtil.error(res, 500, "INTERNAL_ERROR", "The request could not be completed.", null);
      return;
    }
    res.setStatus(500);
    req.setAttribute("errorMessage", "Something went wrong. Please try again.");
    req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, res);
  }

  protected long longParam(HttpServletRequest r, String name) {
    try {
      return Long.parseLong(r.getParameter(name));
    } catch (Exception e) {
      throw new ValidationException(Map.of(name, "Enter a valid number."));
    }
  }

  protected int intParam(HttpServletRequest r, String name, int fallback) {
    try {
      return Integer.parseInt(r.getParameter(name));
    } catch (Exception e) {
      if (r.getParameter(name) == null) {
        return fallback;
      }
      throw new ValidationException(Map.of(name, "Enter a valid whole number."));
    }
  }

  protected void page(HttpServletRequest r, HttpServletResponse s, String view)
      throws ServletException, IOException {
    r.getRequestDispatcher("/WEB-INF/jsp/" + view).forward(r, s);
  }

  protected String path(HttpServletRequest r) {
    return r.getServletPath() + (r.getPathInfo() == null ? "" : r.getPathInfo());
  }
}
