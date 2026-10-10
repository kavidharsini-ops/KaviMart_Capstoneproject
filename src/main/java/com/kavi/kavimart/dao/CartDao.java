package com.kavi.kavimart.dao;

import com.kavi.kavimart.model.CartItem;
import java.util.List;

/**
 * Persistence contract for buyer shopping cart operations.
 */
public interface CartDao {

  /**
   * Retrieves all items currently in a buyer's cart.
   *
   * @param userId the ID of the buyer
   * @return list of items in the cart
   */
  List<CartItem> findByUser(long userId);

  /**
   * Adds an item to the buyer's cart or increments its quantity if already present.
   *
   * @param userId the ID of the buyer
   * @param productId the ID of the product to add
   * @param quantity the quantity to add
   */
  void add(long userId, long productId, int quantity);

  /**
   * Updates the exact quantity of a specific product in the cart.
   *
   * @param userId the ID of the buyer
   * @param productId the ID of the product
   * @param quantity the new quantity
   * @return true if updated, false if not found
   */
  boolean update(long userId, long productId, int quantity);

  /**
   * Removes a single product line from the cart.
   *
   * @param userId the ID of the buyer
   * @param productId the ID of the product to remove
   * @return true if removed, false if not found
   */
  boolean remove(long userId, long productId);

  /**
   * Removes all items from a buyer's cart (typically after checkout).
   *
   * @param userId the ID of the buyer
   */
  void clear(long userId);
}
