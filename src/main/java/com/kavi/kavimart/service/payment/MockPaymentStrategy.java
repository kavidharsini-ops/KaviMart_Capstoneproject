package com.kavi.kavimart.service.payment;
import java.math.BigDecimal;
/** Deterministic development payment strategy; never contacts a real processor. */
public class MockPaymentStrategy implements PaymentStrategy { /** {@inheritDoc} */ public boolean charge(long buyerId,BigDecimal amount){return buyerId>0&&amount!=null&&amount.signum()>=0;} }
