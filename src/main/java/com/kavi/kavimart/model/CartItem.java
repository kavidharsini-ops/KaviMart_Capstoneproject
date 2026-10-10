package com.kavi.kavimart.model;

import java.math.BigDecimal;

/**
 * Line item in a buyer's shopping bag.
 */
public class CartItem {
  private long id;
  private long userId;
  private Product product;
  private int quantity;

  public CartItem() {
  }

  /**
   * Constructs a cart item.
   *
   * @param id primary key ID
   * @param userId buyer ID
   * @param product product details
   * @param quantity item quantity
   */
  public CartItem(long id, long userId, Product product, int quantity) {
    this.id = id;
    this.userId = userId;
    this.product = product;
    this.quantity = quantity;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  /**
   * Computes the line total for this item (price multiplied by quantity).
   *
   * @return total price for this line
   */
  public BigDecimal getLineTotal() {
    return product != null && product.getPrice() != null
        ? product.getPrice().multiply(BigDecimal.valueOf(quantity))
        : BigDecimal.ZERO;
  }
}
