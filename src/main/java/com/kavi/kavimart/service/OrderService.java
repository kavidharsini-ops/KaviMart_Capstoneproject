package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.OrderDao;
import com.kavi.kavimart.dto.OrderSummaryDTO;
import com.kavi.kavimart.exception.ConflictException;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.model.CartItem;
import com.kavi.kavimart.service.payment.PaymentStrategy;
import com.kavi.kavimart.util.ValidationUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service managing checkout, order lifecycle status transitions, and role-based order queries.
 */
public class OrderService {
  private final OrderDao dao;
  private final CartService cartService;
  private final PaymentStrategy payment;

  /**
   * Constructs the order service.
   *
   * @param dao order persistence DAO
   * @param carts cart service for retrieving buyer items
   * @param payment payment strategy implementation
   */
  public OrderService(OrderDao dao, CartService carts, PaymentStrategy payment) {
    this.dao = dao;
    this.cartService = carts;
    this.payment = payment;
  }

  /**
   * Places an order for the buyer after validating non-empty cart and executing payment strategy.
   *
   * @param buyerId the ID of the buyer placing the order
   * @return newly created order summary
   */
  public OrderSummaryDTO placeOrder(long buyerId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(buyerId > 0, "buyerId", "A buyer is required.", e);
    ValidationUtil.throwIfAny(e);

    List<CartItem> items = cartService.getItems(buyerId);
    if (items.isEmpty()) {
      throw new ConflictException("Your cart is empty.");
    }

    BigDecimal amount = items.stream()
        .map(CartItem::getLineTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (!payment.charge(buyerId, amount)) {
      throw new ConflictException("Payment was declined.");
    }

    return dao.placeOrder(buyerId);
  }

  /**
   * Retrieves order history for a specific buyer.
   *
   * @param buyerId the buyer ID
   * @return list of orders belonging to the buyer
   */
  public List<OrderSummaryDTO> buyerOrders(long buyerId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(buyerId > 0, "buyerId", "A buyer is required.", e);
    ValidationUtil.throwIfAny(e);
    return dao.findByBuyer(buyerId);
  }

  /**
   * Retrieves orders containing products listed by the specified seller.
   *
   * @param sellerId the seller ID
   * @return list of matching seller orders
   */
  public List<OrderSummaryDTO> sellerOrders(long sellerId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(sellerId > 0, "sellerId", "A seller is required.", e);
    ValidationUtil.throwIfAny(e);
    return dao.findBySeller(sellerId);
  }

  /**
   * Retrieves all orders for administrative viewing.
   *
   * @return list of all orders
   */
  public List<OrderSummaryDTO> allOrders() {
    return dao.findAll();
  }

  /**
   * Updates an order status following the strict state machine:
   * PENDING -&gt; CONFIRMED -&gt; SHIPPED -&gt; DELIVERED.
   *
   * @param sellerId the seller requesting the transition
   * @param orderId the order ID
   * @param next the target status
   */
  public void updateSellerStatus(long sellerId, long orderId, String next) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(sellerId > 0, "sellerId", "A seller is required.", e);
    ValidationUtil.require(orderId > 0, "orderId", "A valid order is required.", e);
    ValidationUtil.require(Set.of("CONFIRMED", "SHIPPED", "DELIVERED").contains(next),
        "status", "Invalid order status.", e);
    ValidationUtil.throwIfAny(e);

    OrderSummaryDTO order = dao.findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
    if (order.getItems().stream().noneMatch(i -> i.getSellerId() == sellerId)) {
      throw new ResourceNotFoundException("Order not found for this seller.");
    }

    String expected = switch (order.getStatus()) {
      case "PENDING" -> "CONFIRMED";
      case "CONFIRMED" -> "SHIPPED";
      case "SHIPPED" -> "DELIVERED";
      default -> "";
    };

    if (expected.isEmpty() || !expected.equals(next)) {
      throw new ConflictException("Order status can only move from "
          + order.getStatus() + " to " + expected + ".");
    }

    if (!dao.transitionSeller(orderId, sellerId, order.getStatus(), next)) {
      throw new ConflictException("Order status changed. Refresh and try again.");
    }
  }

  /**
   * Cancels a pending order and restores product stock.
   *
   * @param buyerId the buyer ID
   * @param orderId the order ID to cancel
   */
  public void cancel(long buyerId, long orderId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(buyerId > 0, "buyerId", "A buyer is required.", e);
    ValidationUtil.require(orderId > 0, "orderId", "A valid order is required.", e);
    ValidationUtil.throwIfAny(e);
    if (!dao.cancelPending(orderId, buyerId)) {
      throw new ConflictException("Only your pending orders can be cancelled.");
    }
  }

  /**
   * Computes seller dashboard statistics for total orders and revenue.
   *
   * @param sellerId the seller ID
   * @return map with "orders" count and "revenue" total
   */
  public Map<String, BigDecimal> sellerStats(long sellerId) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(sellerId > 0, "sellerId", "A seller is required.", e);
    ValidationUtil.throwIfAny(e);
    return dao.sellerStats(sellerId);
  }
}
