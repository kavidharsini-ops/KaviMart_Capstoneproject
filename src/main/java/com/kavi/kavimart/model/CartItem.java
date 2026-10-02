package com.kavi.kavimart.model;

import java.math.BigDecimal;
/** A buyer's cart line and associated live product details. */
public class CartItem {
 private long id,userId; private Product product; private int quantity;
 public CartItem(){}
 public CartItem(long id,long userId,Product product,int quantity){this.id=id;this.userId=userId;this.product=product;this.quantity=quantity;}
 public long getId(){return id;}public void setId(long v){id=v;}public long getUserId(){return userId;}public void setUserId(long v){userId=v;}public Product getProduct(){return product;}public void setProduct(Product v){product=v;}public int getQuantity(){return quantity;}public void setQuantity(int v){quantity=v;}public BigDecimal getLineTotal(){return product.getPrice().multiply(BigDecimal.valueOf(quantity));}
}
