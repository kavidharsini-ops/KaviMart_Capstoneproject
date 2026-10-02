package com.kavi.kavimart.dao;
import com.kavi.kavimart.model.Review;import java.util.List;
/** Persistence contract for verified product reviews. */
public interface ReviewDao { /** Insert a review. */ Review create(long userId,long productId,int rating,String comment); /** Return reviews for one product. */ List<Review> findByProduct(long productId); /** Calculate the average product rating, zero when there are no reviews. */ double averageRating(long productId); /** Check whether a buyer has a delivered order containing the product. */ boolean hasDeliveredPurchase(long userId,long productId); }
