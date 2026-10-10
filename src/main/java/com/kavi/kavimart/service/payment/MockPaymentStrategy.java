package com.kavi.kavimart.service.payment;

import java.math.BigDecimal;

/**
 * Mock development payment strategy that simulates successful transactions
 * without calling external APIs.
 */
public class MockPaymentStrategy implements PaymentStrategy {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean charge(long buyerId, BigDecimal amount) {
    return buyerId > 0 && amount != null && amount.signum() >= 0;
  }
}
