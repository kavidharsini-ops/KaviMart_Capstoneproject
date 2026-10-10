package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.service.ReviewService;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller handling verified-purchase review submission from the product detail page.
 */
public class ReviewServlet extends BaseServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      UserResponseDTO user = user(req);
      ReviewService reviews = service(getServletContext(), SessionKeys.REVIEW_SERVICE, ReviewService.class);
      long productId = longParam(req, "productId");
      reviews.create(user.getId(), productId, intParam(req, "rating", 0), req.getParameter("comment"));
      res.sendRedirect(req.getContextPath() + "/products/" + productId + "#reviews");
    } catch (Throwable t) {
      error(req, res, t);
    }
  }
}
