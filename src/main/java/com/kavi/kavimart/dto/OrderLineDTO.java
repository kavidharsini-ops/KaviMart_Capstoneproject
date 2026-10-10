package com.kavi.kavimart.dto;

import java.math.BigDecimal;

/** Purchased product line with the price captured at checkout. */
public final class OrderLineDTO {
  private final long productId;
  private final String productName;
  private final int quantity;
  private final BigDecimal unitPrice;
  private final long sellerId;

  /** Constructs an order line DTO. */
  public OrderLineDTO(long productId, String productName, int quantity, BigDecimal unitPrice,
      long sellerId) {
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.sellerId = sellerId;
  }

  public long getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public long getSellerId() {
    return sellerId;
  }
}
