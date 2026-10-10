package com.kavi.kavimart.model;

import java.time.LocalDateTime;

/** Buyer product review. */
public class Review {
  private long id;
  private long productId;
  private long userId;
  private String userName;
  private int rating;
  private String comment;
  private LocalDateTime createdAt;

  public Review() { }

  /** Constructs a review model. */
  public Review(long id, long productId, long userId, String userName, int rating, String comment,
      LocalDateTime createdAt) {
    this.id = id;
    this.productId = productId;
    this.userId = userId;
    this.userName = userName;
    this.rating = rating;
    this.comment = comment;
    this.createdAt = createdAt;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getProductId() {
    return productId;
  }

  public void setProductId(long productId) {
    this.productId = productId;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
