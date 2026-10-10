package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.service.OrderService;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller handling buyer order history, seller incoming orders, and seller dashboard.
 */
public class OrderServlet extends BaseServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      UserResponseDTO user = user(req);
      OrderService orders = service(getServletContext(), SessionKeys.ORDER_SERVICE, OrderService.class);

      if (req.getServletPath().startsWith("/seller")) {
        req.setAttribute("orders", orders.sellerOrders(user.getId()));
        req.setAttribute("stats", orders.sellerStats(user.getId()));
        String view = req.getServletPath().equals("/seller/dashboard")
            ? "seller/dashboard.jsp"
            : "seller/orders.jsp";
        page(req, res, view);
      } else {
        req.setAttribute("orders", orders.buyerOrders(user.getId()));
        page(req, res, "buyer/orders.jsp");
      }
    } catch (Throwable t) {
      error(req, res, t);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      UserResponseDTO user = user(req);
      OrderService orders = service(getServletContext(), SessionKeys.ORDER_SERVICE, OrderService.class);

      if (req.getServletPath().startsWith("/seller")) {
        orders.updateSellerStatus(user.getId(), longParam(req, "orderId"), req.getParameter("status"));
        res.sendRedirect(req.getContextPath() + "/seller/orders");
      } else if ("cancel".equals(req.getParameter("action"))) {
        orders.cancel(user.getId(), longParam(req, "orderId"));
        res.sendRedirect(req.getContextPath() + "/buyer/orders");
      } else {
        orders.placeOrder(user.getId());
        res.sendRedirect(req.getContextPath() + "/buyer/orders?placed=1");
      }
    } catch (Throwable t) {
      error(req, res, t);
    }
  }
}
