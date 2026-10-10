package com.kavi.kavimart.dao;

import com.kavi.kavimart.model.Review;
import java.util.List;

/**
 * Persistence contract for verified-purchase product reviews.
 */
public interface ReviewDao {

  /**
   * Persists a new product review submitted by a verified buyer.
   *
   * @param userId the ID of the buyer writing the review
   * @param productId the ID of the product reviewed
   * @param rating numerical rating from 1 to 5
   * @param comment review commentary text
   * @return the created Review model entity
   */
  Review create(long userId, long productId, int rating, String comment);

  /**
   * Retrieves all reviews written for a specific product, ordered newest first.
   *
   * @param productId the ID of the product
   * @return list of reviews
   */
  List<Review> findByProduct(long productId);

  /**
   * Computes the mathematical average rating for a product (0.0 if no reviews exist).
   *
   * @param productId the ID of the product
   * @return average rating
   */
  double averageRating(long productId);

  /**
   * Verifies whether the specified user has a delivered order containing this product.
   *
   * @param userId the ID of the buyer
   * @param productId the ID of the product
   * @return true if the buyer has purchased and received the product, false otherwise
   */
  boolean hasDeliveredPurchase(long userId, long productId);
}
