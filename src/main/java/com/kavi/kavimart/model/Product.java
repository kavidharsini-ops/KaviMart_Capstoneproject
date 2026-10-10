package com.kavi.kavimart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Marketplace product entity. */
public class Product {
  private long id;
  private long sellerId;
  private String sellerName;
  private String name;
  private String description;
  private String category;
  private String imageUrl;
  private BigDecimal price;
  private int stockQty;
  private LocalDateTime createdAt;

  public Product() { }

  /** Constructs a product model. */
  public Product(long id, long sellerId, String sellerName, String name, String description,
      BigDecimal price, int stockQty, String category, String imageUrl, LocalDateTime createdAt) {
    this.id = id;
    this.sellerId = sellerId;
    this.sellerName = sellerName;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stockQty = stockQty;
    this.category = category;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getSellerId() {
    return sellerId;
  }

  public void setSellerId(long sellerId) {
    this.sellerId = sellerId;
  }

  public String getSellerName() {
    return sellerName;
  }

  public void setSellerName(String sellerName) {
    this.sellerName = sellerName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public int getStockQty() {
    return stockQty;
  }

  public void setStockQty(int stockQty) {
    this.stockQty = stockQty;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
