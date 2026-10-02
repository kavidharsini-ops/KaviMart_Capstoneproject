package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.ProductDao;import com.kavi.kavimart.dao.ReviewDao;import com.kavi.kavimart.exception.AppException;import com.kavi.kavimart.model.Review;import com.kavi.kavimart.util.ValidationUtil;import java.util.*;
/** Verified-purchase review rules for product detail pages. */
public class ReviewService {
 private final ReviewDao reviews;private final ProductDao products;
 /** Create the service with DAO interfaces. */ public ReviewService(ReviewDao reviews,ProductDao products){this.reviews=reviews;this.products=products;}
 /** Create one review after validating rating, comment, and delivered purchase. */ public Review create(long userId,long productId,int rating,String comment){Map<String,String>e=ValidationUtil.errors();ValidationUtil.require(userId>0,"userId","A buyer is required.",e);ValidationUtil.require(productId>0,"productId","A valid product is required.",e);ValidationUtil.require(rating>=1&&rating<=5,"rating","Rating must be from 1 to 5.",e);ValidationUtil.require(comment==null||comment.length()<=2000,"comment","Review is too long.",e);ValidationUtil.throwIfAny(e);if(products.findById(productId).isEmpty())throw new com.kavi.kavimart.exception.ResourceNotFoundException("Product not found.");if(!reviews.hasDeliveredPurchase(userId,productId))throw new AppException("FORBIDDEN","Reviews are available after a delivered purchase.",403);return reviews.create(userId,productId,rating,comment==null?"":comment.trim());}
 /** List reviews for the product. */ public List<Review> list(long productId){Map<String,String>e=ValidationUtil.errors();ValidationUtil.require(productId>0,"productId","A valid product is required.",e);ValidationUtil.throwIfAny(e);return reviews.findByProduct(productId);}
 /** Calculate average rating for display. */ public double average(long productId){Map<String,String>e=ValidationUtil.errors();ValidationUtil.require(productId>0,"productId","A valid product is required.",e);ValidationUtil.throwIfAny(e);return reviews.averageRating(productId);}
}
