package com.kavi.kavimart.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kavi.kavimart.dao.jdbc.JdbcCartDao;
import com.kavi.kavimart.dao.jdbc.JdbcOrderDao;
import com.kavi.kavimart.dao.jdbc.JdbcProductDao;
import com.kavi.kavimart.exception.ConflictException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderDaoTest {

  @Test
  void checkoutCapturesPriceDeductsStockAndClearsCart() {
    var ds = TestDatabase.create("ordercheckout");
    JdbcCartDao cart = new JdbcCartDao(ds);
    JdbcOrderDao orders = new JdbcOrderDao(ds);
    cart.add(4, 101, 2);
    var result = orders.placeOrder(4);
    assertEquals(new BigDecimal("1798.00"), result.getTotalAmount());
    assertEquals(18, new JdbcProductDao(ds).findById(101).orElseThrow().getStockQty());
    assertTrue(cart.findByUser(4).isEmpty());
    assertEquals(1, orders.findByBuyer(4).size());
  }

  @Test
  void insufficientStockRollsBackTheEntireCheckout() {
    var ds = TestDatabase.create("orderrollback");
    JdbcCartDao cart = new JdbcCartDao(ds);
    JdbcOrderDao orders = new JdbcOrderDao(ds);
    cart.add(4, 101, 100);
    assertThrows(ConflictException.class, () -> orders.placeOrder(4));
    assertEquals(0, orders.findByBuyer(4).size());
    assertEquals(20, new JdbcProductDao(ds).findById(101).orElseThrow().getStockQty());
    assertEquals(100, cart.findByUser(4).get(0).getQuantity());
  }
}
