package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.CartDao;
import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.exception.ConflictException;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.model.CartItem;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.util.ValidationUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service managing buyer shopping cart business logic including stock-validation rules.
 */
public class CartService {
  private final CartDao carts;
  private final ProductDao products;

  /**
   * Constructs the cart service with the required DAOs.
   *
   * @param carts DAO for shopping cart persistence
   * @param products DAO for product catalog queries
   */
  public CartService(CartDao carts, ProductDao products) {
    this.carts = carts;
    this.products = products;
  }

  /**
   * Retrieves all items currently in the buyer's shopping cart.
   *
   * @param userId the ID of the buyer
   * @return list of items in the cart
   */
  public List<CartItem> getItems(long userId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(userId > 0, "userId", "A buyer is required.", e);
    ValidationUtil.throwIfAny(e);
    return carts.findByUser(userId);
  }

  /**
   * Adds a product to the cart with quantity validation against available inventory.
   *
   * @param userId the ID of the buyer
   * @param productId the ID of the product
   * @param quantity the quantity to add
   */
  public void add(long userId, long productId, int quantity) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(userId > 0, "userId", "A buyer is required.", e);
    ValidationUtil.require(productId > 0, "productId", "A valid product is required.", e);
    ValidationUtil.require(quantity > 0 && quantity <= 99,
        "quantity", "Quantity must be between 1 and 99.", e);
    ValidationUtil.throwIfAny(e);

    Product p = products.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
    int current = carts.findByUser(userId).stream()
        .filter(i -> i.getProduct().getId() == productId)
        .mapToInt(CartItem::getQuantity)
        .sum();
    if (current + quantity > p.getStockQty()) {
      throw new ConflictException("Requested quantity exceeds available stock.");
    }
    carts.add(userId, productId, quantity);
  }

  /**
   * Updates the quantity of an item already in the cart with stock validation.
   *
   * @param userId the ID of the buyer
   * @param productId the ID of the product
   * @param quantity the new desired quantity
   */
  public void update(long userId, long productId, int quantity) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(userId > 0, "userId", "A buyer is required.", e);
    ValidationUtil.require(productId > 0, "productId", "A valid product is required.", e);
    ValidationUtil.require(quantity > 0 && quantity <= 99,
        "quantity", "Quantity must be between 1 and 99.", e);
    ValidationUtil.throwIfAny(e);

    Product p = products.findById(productId)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found."));
    if (quantity > p.getStockQty()) {
      throw new ConflictException("Requested quantity exceeds available stock.");
    }
    if (!carts.update(userId, productId, quantity)) {
      throw new ResourceNotFoundException("Cart item not found.");
    }
  }

  /**
   * Removes a product line from the buyer's shopping cart.
   *
   * @param userId the ID of the buyer
   * @param productId the ID of the product to remove
   */
  public void remove(long userId, long productId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(userId > 0, "userId", "A buyer is required.", e);
    ValidationUtil.require(productId > 0, "productId", "A valid product is required.", e);
    ValidationUtil.throwIfAny(e);
    if (!carts.remove(userId, productId)) {
      throw new ResourceNotFoundException("Cart item not found.");
    }
  }

  /**
   * Calculates the grand total amount for all items in the buyer's cart.
   *
   * @param userId the ID of the buyer
   * @return total monetary amount
   */
  public BigDecimal total(long userId) {
    return getItems(userId).stream()
        .map(CartItem::getLineTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
