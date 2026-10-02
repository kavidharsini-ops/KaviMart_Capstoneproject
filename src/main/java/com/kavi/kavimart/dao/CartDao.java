package com.kavi.kavimart.dao;
import com.kavi.kavimart.model.CartItem;import java.util.List;
/** Persistence contract for buyer carts. */
public interface CartDao { /** Return a buyer's current cart lines. */ List<CartItem> findByUser(long userId); /** Insert or add to a cart line. */ void add(long userId,long productId,int quantity); /** Set a cart line quantity. */ boolean update(long userId,long productId,int quantity); /** Remove one line. */ boolean remove(long userId,long productId); /** Remove all cart lines for checkout. */ void clear(long userId); }
