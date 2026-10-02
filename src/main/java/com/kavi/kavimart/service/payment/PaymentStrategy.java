package com.kavi.kavimart.service.payment;
import java.math.BigDecimal;
/** Payment provider contract used by checkout. */
public interface PaymentStrategy { /** Charge the customer's order total; false means payment was declined. */ boolean charge(long buyerId,BigDecimal amount); }
