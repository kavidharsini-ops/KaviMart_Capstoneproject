package com.kavi.kavimart.service.payment;
/** Small factory for the configured payment strategy. */
public final class PaymentStrategyFactory { private PaymentStrategyFactory(){} /** Return the mock strategy selected for this development build. */ public static PaymentStrategy create(){return new MockPaymentStrategy();} }
