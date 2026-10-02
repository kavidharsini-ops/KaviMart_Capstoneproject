package com.kavi.kavimart.dao;

import com.kavi.kavimart.dao.jdbc.*;import com.kavi.kavimart.exception.ConflictException;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class OrderDaoTest {
 @Test void checkoutCapturesPriceDeductsStockAndClearsCart(){var ds=TestDatabase.create("ordercheckout");JdbcCartDao cart=new JdbcCartDao(ds);JdbcOrderDao orders=new JdbcOrderDao(ds);cart.add(4,101,2);var result=orders.placeOrder(4);assertEquals(new java.math.BigDecimal("68.00"),result.getTotalAmount());assertEquals(16,new JdbcProductDao(ds).findById(101).orElseThrow().getStockQty());assertTrue(cart.findByUser(4).isEmpty());assertEquals(1,orders.findByBuyer(4).size());}
 @Test void insufficientStockRollsBackTheEntireCheckout(){var ds=TestDatabase.create("orderrollback");JdbcCartDao cart=new JdbcCartDao(ds);JdbcOrderDao orders=new JdbcOrderDao(ds);cart.add(4,101,100);assertThrows(ConflictException.class,()->orders.placeOrder(4));assertEquals(0,orders.findByBuyer(4).size());assertEquals(18,new JdbcProductDao(ds).findById(101).orElseThrow().getStockQty());assertEquals(100,cart.findByUser(4).get(0).getQuantity());}
}
