package com.kavi.kavimart.controller;

import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.model.PageResult;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.model.ProductQuery;
import com.kavi.kavimart.service.ProductService;
import com.kavi.kavimart.service.ReviewService;
import com.kavi.kavimart.util.SessionKeys;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller handling public product catalog browsing, product details, and seller listing management.
 */
public class ProductServlet extends BaseServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      ProductService products = service(getServletContext(), SessionKeys.PRODUCT_SERVICE, ProductService.class);
      String servlet = req.getServletPath();

      if (servlet.startsWith("/seller/products")) {
        sellerPage(req, res, products);
        return;
      }

      if ("/catalog".equals(servlet)) {
        String keyword = req.getParameter("q");
        String category = req.getParameter("category");
        String sort = req.getParameter("sort");
        int page = intParam(req, "page", 1);
        PageResult<Product> result = products.search(new ProductQuery(keyword, category, sort, page, 12));
        req.setAttribute("products", result);
        req.setAttribute("categories", List.of("Toys", "Beauty", "Jewellery"));
        page(req, res, "buyer/catalog.jsp");
        return;
      }

      String info = req.getPathInfo();
      if (info == null || info.equals("/")) {
        res.sendRedirect(req.getContextPath() + "/catalog");
        return;
      }

      long id;
      try {
        id = Long.parseLong(info.substring(1));
      } catch (NumberFormatException e) {
        throw new ResourceNotFoundException("Product not found.");
      }

      Product product = products.get(id);
      ReviewService reviews = service(getServletContext(), SessionKeys.REVIEW_SERVICE, ReviewService.class);
      req.setAttribute("product", product);
      req.setAttribute("reviews", reviews.list(id));
      req.setAttribute("averageRating", reviews.average(id));
      page(req, res, "buyer/product-detail.jsp");
    } catch (Throwable t) {
      error(req, res, t);
    }
  }

  private void sellerPage(HttpServletRequest req, HttpServletResponse res, ProductService products)
      throws ServletException, IOException {
    UserResponseDTO user = user(req);
    String edit = req.getParameter("edit");
    req.setAttribute("categories", List.of("Toys", "Beauty", "Jewellery"));

    if (edit != null) {
      Product p = products.get(Long.parseLong(edit));
      if (p.getSellerId() != user.getId()) {
        throw new ResourceNotFoundException("Product not found.");
      }
      req.setAttribute("product", p);
      page(req, res, "seller/product-form.jsp");
      return;
    }

    if ("new".equals(req.getParameter("new"))) {
      page(req, res, "seller/product-form.jsp");
      return;
    }

    req.setAttribute("products", products.listSeller(user.getId()));
    page(req, res, "seller/products.jsp");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      UserResponseDTO user = user(req);
      ProductService products = service(getServletContext(), SessionKeys.PRODUCT_SERVICE, ProductService.class);
      String action = req.getParameter("action");

      if ("delete".equals(action)) {
        products.delete(user.getId(), longParam(req, "id"));
      } else {
        Product p = new Product();
        p.setName(req.getParameter("name"));
        p.setDescription(req.getParameter("description"));
        p.setPrice(decimal(req.getParameter("price")));
        p.setStockQty(intParam(req, "stock", -1));
        p.setCategory(req.getParameter("category"));
        p.setImageUrl(req.getParameter("imageUrl"));
        String id = req.getParameter("id");
        if (id == null || id.isBlank()) {
          products.create(user.getId(), p);
        } else {
          p.setId(Long.parseLong(id));
          products.update(user.getId(), p);
        }
      }
      res.sendRedirect(req.getContextPath() + "/seller/products");
    } catch (Throwable t) {
      error(req, res, t);
    }
  }

  private BigDecimal decimal(String raw) {
    try {
      return new BigDecimal(raw);
    } catch (Exception e) {
      throw new ValidationException(Map.of("price", "Enter a valid price."));
    }
  }
}
