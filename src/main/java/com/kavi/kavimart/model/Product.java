package com.kavi.kavimart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/** Marketplace product entity. */
public class Product {
 private long id,sellerId; private String sellerName,name,description,category,imageUrl; private BigDecimal price; private int stockQty; private LocalDateTime createdAt;
 public Product(){}
 public Product(long id,long sellerId,String sellerName,String name,String description,BigDecimal price,int stockQty,String category,String imageUrl,LocalDateTime createdAt){this.id=id;this.sellerId=sellerId;this.sellerName=sellerName;this.name=name;this.description=description;this.price=price;this.stockQty=stockQty;this.category=category;this.imageUrl=imageUrl;this.createdAt=createdAt;}
 public long getId(){return id;}public void setId(long v){id=v;}public long getSellerId(){return sellerId;}public void setSellerId(long v){sellerId=v;}public String getSellerName(){return sellerName;}public void setSellerName(String v){sellerName=v;}public String getName(){return name;}public void setName(String v){name=v;}public String getDescription(){return description;}public void setDescription(String v){description=v;}public BigDecimal getPrice(){return price;}public void setPrice(BigDecimal v){price=v;}public int getStockQty(){return stockQty;}public void setStockQty(int v){stockQty=v;}public String getCategory(){return category;}public void setCategory(String v){category=v;}public String getImageUrl(){return imageUrl;}public void setImageUrl(String v){imageUrl=v;}public LocalDateTime getCreatedAt(){return createdAt;}public void setCreatedAt(LocalDateTime v){createdAt=v;}
}
