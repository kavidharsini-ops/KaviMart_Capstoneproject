package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.model.PageResult;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.model.ProductQuery;
import com.kavi.kavimart.util.ValidationUtil;
import java.util.List;
import java.util.Map;

/**
 * Service managing catalog search, pagination, and seller listing operations.
 */
public class ProductService {
  private final ProductDao dao;

  /**
   * Constructs the product service.
   *
   * @param dao product persistence DAO
   */
  public ProductService(ProductDao dao) {
    this.dao = dao;
  }

  /**
   * Searches active product listings with input validation and pagination rules.
   *
   * @param q product query filters
   * @return paginated product search result
   */
  public PageResult<Product> search(ProductQuery q) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(q != null, "query", "A search query is required.", e);
    if (q != null) {
      ValidationUtil.require(q.getKeyword().length() <= 100,
          "keyword", "Search text is too long.", e);
      ValidationUtil.require(q.getCategory().length() <= 50,
          "category", "Category is too long.", e);
      ValidationUtil.require(q.getPage() > 0 && q.getPageSize() > 0 && q.getPageSize() <= 48,
          "page", "Page values are invalid.", e);
    }
    ValidationUtil.throwIfAny(e);
    return dao.search(q);
  }

  /**
   * Retrieves an active product listing by its ID.
   *
   * @param id the product ID
   * @return the Product model entity
   */
  public Product get(long id) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(id > 0, "id", "A valid product is required.", e);
    ValidationUtil.throwIfAny(e);
    return dao.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
  }

  /**
   * Lists all products listed by a particular seller.
   *
   * @param sellerId the seller ID
   * @return list of seller products
   */
  public List<Product> listSeller(long sellerId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(sellerId > 0, "sellerId", "A valid seller is required.", e);
    ValidationUtil.throwIfAny(e);
    return dao.findBySeller(sellerId);
  }

  /**
   * Validates and persists a new product listing for a seller.
   *
   * @param sellerId the seller ID
   * @param p product details
   * @return created Product entity
   */
  public Product create(long sellerId, Product p) {
    validateProduct(sellerId, p, false);
    p.setSellerId(sellerId);
    return dao.create(p);
  }

  /**
   * Validates and updates an existing listing owned by the seller.
   *
   * @param sellerId the seller ID
   * @param p product details with updated values
   */
  public void update(long sellerId, Product p) {
    validateProduct(sellerId, p, true);
    if (!dao.updateOwned(p, sellerId)) {
      throw new ResourceNotFoundException("Product not found or not owned by this seller.");
    }
  }

  /**
   * Deactivates (soft deletes) a listing owned by the seller.
   *
   * @param sellerId the seller ID
   * @param productId the product ID
   */
  public void delete(long sellerId, long productId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(sellerId > 0, "sellerId", "Seller is required.", e);
    ValidationUtil.require(productId > 0, "id", "A valid product is required.", e);
    ValidationUtil.throwIfAny(e);
    if (!dao.deleteOwned(productId, sellerId)) {
      throw new ResourceNotFoundException("Product not found or not owned by this seller.");
    }
  }

  /**
   * Deactivates (soft deletes) any listing as an administrator.
   *
   * @param productId the product ID to remove
   */
  public void adminDelete(long productId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(productId > 0, "id", "A valid product is required.", e);
    ValidationUtil.throwIfAny(e);
    if (!dao.deleteAny(productId)) {
      throw new ResourceNotFoundException("Product not found.");
    }
  }

  /**
   * Lists all active products across all sellers for administrative moderation.
   *
   * @return list of all products
   */
  public List<Product> listAll() {
    return dao.findAll();
  }

  private void validateProduct(long sellerId, Product p, boolean requireId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(sellerId > 0, "sellerId", "A valid seller is required.", e);
    ValidationUtil.require(p != null, "product", "Product details are required.", e);
    if (p != null) {
      if (requireId) {
        ValidationUtil.require(p.getId() > 0, "id", "A valid product is required.", e);
      }
      ValidationUtil.require(p.getName() != null && !p.getName().trim().isEmpty() && p.getName().length() <= 200,
          "name", "Name is required (200 characters maximum).", e);
      ValidationUtil.require(p.getDescription() == null || p.getDescription().length() <= 5000,
          "description", "Description is too long.", e);
      ValidationUtil.require(ValidationUtil.validPrice(p.getPrice()),
          "price", "Price must be zero or greater with at most two decimal places.", e);
      ValidationUtil.require(p.getStockQty() >= 0,
          "stock", "Stock must be zero or greater.", e);
      ValidationUtil.require(p.getCategory() != null && !p.getCategory().isBlank()
          && p.getCategory().length() <= 50, "category", "Choose a category.", e);
      ValidationUtil.require(p.getImageUrl() == null || p.getImageUrl().isBlank()
          || p.getImageUrl().length() <= 500, "imageUrl", "Image URL is too long.", e);
    }
    ValidationUtil.throwIfAny(e);
  }
}
