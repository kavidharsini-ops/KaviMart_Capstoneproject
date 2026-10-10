package com.kavi.kavimart.controller;

import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.service.OrderService;
import com.kavi.kavimart.service.ProductService;
import com.kavi.kavimart.service.UserService;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Administrative user moderation, order oversight, and listing moderation controller.
 */
public class AdminServlet extends BaseServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      UserService userService = service(getServletContext(), SessionKeys.USER_SERVICE, UserService.class);
      OrderService orderService = service(getServletContext(), SessionKeys.ORDER_SERVICE, OrderService.class);
      ProductService productService = service(getServletContext(), SessionKeys.PRODUCT_SERVICE, ProductService.class);

      req.setAttribute("users", userService.listUsers());
      req.setAttribute("orders", orderService.allOrders());
      req.setAttribute("products", productService.listAll());
      page(req, res, "admin/index.jsp");
    } catch (Throwable t) {
      error(req, res, t);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      String action = req.getParameter("action");
      UserService userService = service(getServletContext(), SessionKeys.USER_SERVICE, UserService.class);
      ProductService productService = service(getServletContext(), SessionKeys.PRODUCT_SERVICE, ProductService.class);

      if ("activate".equals(action) || "ban".equals(action)) {
        userService.setActive(longParam(req, "userId"), "activate".equals(action));
      } else if ("remove-listing".equals(action)) {
        productService.adminDelete(longParam(req, "productId"));
      } else {
        throw new ValidationException(Map.of("action", "Choose a valid admin action."));
      }

      res.sendRedirect(req.getContextPath() + "/admin");
    } catch (Throwable t) {
      error(req, res, t);
    }
  }
}
