package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.model.CartItem;
import com.kavi.kavimart.service.CartService;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller rendering the buyer shopping cart view; API edits handled via ApiServlet.
 */
public class CartServlet extends BaseServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      UserResponseDTO user = user(req);
      CartService carts = service(getServletContext(), SessionKeys.CART_SERVICE, CartService.class);
      List<CartItem> items = carts.getItems(user.getId());

      req.setAttribute("items", items);
      req.setAttribute("total", carts.total(user.getId()));
      page(req, res, "buyer/cart.jsp");
    } catch (Throwable t) {
      error(req, res, t);
    }
  }
}
