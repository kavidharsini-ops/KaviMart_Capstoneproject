package com.kavi.kavimart.service.payment;

/**
 * Factory creating configured PaymentStrategy instances.
 */
public final class PaymentStrategyFactory {

  private PaymentStrategyFactory() {
  }

  /**
   * Creates the default PaymentStrategy instance for development and testing.
   *
   * @return the configured PaymentStrategy implementation
   */
  public static PaymentStrategy create() {
    return new MockPaymentStrategy();
  }
}
