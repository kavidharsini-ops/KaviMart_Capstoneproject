package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.exception.AppException;
import com.kavi.kavimart.service.UserService;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Authentication controller handling login, registration, and logout flows.
 */
public class AuthServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;

  /**
   * Displays the sign-in or registration page.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String path = path(req);
    if (path.endsWith("register")) {
      page(req, res, "auth/register.jsp");
    } else {
      page(req, res, "auth/login.jsp");
    }
  }

  /**
   * Authenticates or registers a user. Session ID is rotated upon login.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String path = path(req);
    if (path.endsWith("logout")) {
      HttpSession old = req.getSession(false);
      if (old != null) {
        old.invalidate();
      }
      res.sendRedirect(req.getContextPath() + "/catalog");
      return;
    }

    try {
      UserService users = service(getServletContext(), SessionKeys.USER_SERVICE, UserService.class);
      UserResponseDTO user;
      if (path.endsWith("register")) {
        user = users.register(req.getParameter("name"), req.getParameter("email"),
            req.getParameter("password"), req.getParameter("role"));
      } else {
        user = users.login(req.getParameter("email"), req.getParameter("password"));
      }

      HttpSession session = req.getSession(true);
      req.changeSessionId();
      session.setAttribute(SessionKeys.USER, user);

      String destination = switch (user.getRole()) {
        case "SELLER" -> "/seller/dashboard";
        case "ADMIN" -> "/admin";
        default -> "/catalog";
      };
      res.sendRedirect(req.getContextPath() + destination);
    } catch (AppException e) {
      if (path.endsWith("register")) {
        res.setStatus(e.getStatus());
        req.setAttribute("errorMessage", e.getMessage());
        req.setAttribute("name", req.getParameter("name"));
        req.setAttribute("email", req.getParameter("email"));
        req.setAttribute("role", req.getParameter("role"));
        page(req, res, "auth/register.jsp");
      } else {
        res.setStatus(e.getStatus());
        req.setAttribute("errorMessage", e.getMessage());
        req.setAttribute("email", req.getParameter("email"));
        page(req, res, "auth/login.jsp");
      }
    } catch (Throwable t) {
      error(req, res, t);
    }
  }
}
