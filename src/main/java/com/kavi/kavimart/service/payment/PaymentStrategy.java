package com.kavi.kavimart.service.payment;

import java.math.BigDecimal;

/**
 * Strategy interface representing a payment processing provider.
 */
public interface PaymentStrategy {

  /**
   * Charges the customer's payment method for the specified order total.
   *
   * @param buyerId the ID of the buyer paying
   * @param amount the total amount to charge
   * @return true if the charge succeeded, false if declined
   */
  boolean charge(long buyerId, BigDecimal amount);
}
