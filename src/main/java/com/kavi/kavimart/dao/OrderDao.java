package com.kavi.kavimart.dao;

import com.kavi.kavimart.dto.OrderSummaryDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Persistence contract for transactional orders, lifecycle transitions, and seller statistics.
 */
public interface OrderDao {

  /**
   * Places an order from the buyer's current shopping cart in an atomic transaction.
   *
   * @param buyerId the ID of the buyer checking out
   * @return the newly created order summary
   */
  OrderSummaryDTO placeOrder(long buyerId);

  /**
   * Finds a complete order by its unique primary key.
   *
   * @param id the order ID
   * @return optional containing the order summary if found
   */
  Optional<OrderSummaryDTO> findById(long id);

  /**
   * Retrieves order history for a specific buyer.
   *
   * @param buyerId the buyer ID
   * @return list of orders belonging to the buyer
   */
  List<OrderSummaryDTO> findByBuyer(long buyerId);

  /**
   * Retrieves all orders that include products belonging to the specified seller.
   *
   * @param sellerId the seller ID
   * @return list of matching orders
   */
  List<OrderSummaryDTO> findBySeller(long sellerId);

  /**
   * Retrieves all orders in the system for administrative moderation.
   *
   * @return list of all orders
   */
  List<OrderSummaryDTO> findAll();

  /**
   * Advances an order status with optimistic checking of current status and seller ownership.
   *
   * @param orderId the order ID
   * @param sellerId the seller ID making the transition
   * @param expected the current status expected in the database
   * @param next the target status to transition into
   * @return true if transitioned successfully, false otherwise
   */
  boolean transitionSeller(long orderId, long sellerId, String expected, String next);

  /**
   * Cancels a pending order and restores stock back to product inventory in a single transaction.
   *
   * @param orderId the order ID
   * @param buyerId the buyer ID who owns the order
   * @return true if cancelled successfully, false otherwise
   */
  boolean cancelPending(long orderId, long buyerId);

  /**
   * Computes seller statistics including total order count and aggregate non-cancelled revenue.
   *
   * @param sellerId the seller ID
   * @return map with "orders" count and "revenue" total
   */
  Map<String, BigDecimal> sellerStats(long sellerId);
}
