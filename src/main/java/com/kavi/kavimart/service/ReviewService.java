package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.dao.ReviewDao;
import com.kavi.kavimart.exception.AppException;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.model.Review;
import com.kavi.kavimart.util.ValidationUtil;
import java.util.List;
import java.util.Map;

/**
 * Service managing product reviews and verified-purchase constraints.
 */
public class ReviewService {
  private final ReviewDao reviews;
  private final ProductDao products;

  /**
   * Constructs the review service.
   *
   * @param reviews review persistence DAO
   * @param products product persistence DAO
   */
  public ReviewService(ReviewDao reviews, ProductDao products) {
    this.reviews = reviews;
    this.products = products;
  }

  /**
   * Submits a new review after verifying rating limits, length, and delivered purchase.
   *
   * @param userId the ID of the buyer writing the review
   * @param productId the ID of the product
   * @param rating numerical rating from 1 to 5
   * @param comment review commentary
   * @return the created Review entity
   */
  public Review create(long userId, long productId, int rating, String comment) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(userId > 0, "userId", "A buyer is required.", e);
    ValidationUtil.require(productId > 0, "productId", "A valid product is required.", e);
    ValidationUtil.require(rating >= 1 && rating <= 5, "rating", "Rating must be from 1 to 5.", e);
    ValidationUtil.require(comment == null || comment.length() <= 2000,
        "comment", "Review is too long.", e);
    ValidationUtil.throwIfAny(e);

    if (products.findById(productId).isEmpty()) {
      throw new ResourceNotFoundException("Product not found.");
    }
    if (!reviews.hasDeliveredPurchase(userId, productId)) {
      throw new AppException("FORBIDDEN", "Reviews are available after a delivered purchase.", 403);
    }
    return reviews.create(userId, productId, rating, comment == null ? "" : comment.trim());
  }

  /**
   * Retrieves all reviews written for a product.
   *
   * @param productId the product ID
   * @return list of reviews
   */
  public List<Review> list(long productId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(productId > 0, "productId", "A valid product is required.", e);
    ValidationUtil.throwIfAny(e);
    return reviews.findByProduct(productId);
  }

  /**
   * Computes the average rating score for display on the product page.
   *
   * @param productId the ID of the product
   * @return average rating
   */
  public double average(long productId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(productId > 0, "productId", "A valid product is required.", e);
    ValidationUtil.throwIfAny(e);
    return reviews.averageRating(productId);
  }
}
