package com.kavi.kavimart.model;

import java.time.LocalDateTime;
/** Buyer product review. */
public class Review {
 private long id,productId,userId; private String userName,comment; private int rating; private LocalDateTime createdAt;
 public Review(){}
 public Review(long id,long productId,long userId,String userName,int rating,String comment,LocalDateTime createdAt){this.id=id;this.productId=productId;this.userId=userId;this.userName=userName;this.rating=rating;this.comment=comment;this.createdAt=createdAt;}
 public long getId(){return id;}public long getProductId(){return productId;}public long getUserId(){return userId;}public String getUserName(){return userName;}public int getRating(){return rating;}public String getComment(){return comment;}public LocalDateTime getCreatedAt(){return createdAt;}
}
