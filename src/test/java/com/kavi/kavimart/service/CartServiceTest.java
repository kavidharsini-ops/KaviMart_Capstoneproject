package com.kavi.kavimart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.dao.CartDao;
import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.exception.ConflictException;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.model.CartItem;
import com.kavi.kavimart.model.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  CartDao cartDao;

  @Mock
  ProductDao productDao;

  @InjectMocks
  CartService cartService;

  @Test
  void invalidUserIdThrowsValidationException() {
    assertThrows(ValidationException.class, () -> cartService.getItems(0L));
    verifyNoInteractions(cartDao);
  }

  @Test
  void addingNonExistentProductThrowsResourceNotFoundException() {
    when(productDao.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        cartService.add(1L, 999L, 1));
  }

  @Test
  void addingQuantityExceedingStockThrowsConflictException() {
    Product p = new Product(101L, 2L, "Seller", "Silk Scarf", "Desc",
        new BigDecimal("499.00"), 5, "Accessories", null, LocalDateTime.now());
    when(productDao.findById(101L)).thenReturn(Optional.of(p));
    when(cartDao.findByUser(1L)).thenReturn(List.of());

    assertThrows(ConflictException.class, () ->
        cartService.add(1L, 101L, 10));
  }

  @Test
  void successfullyAddsItemToCart() {
    Product p = new Product(101L, 2L, "Seller", "Silk Scarf", "Desc",
        new BigDecimal("499.00"), 10, "Accessories", null, LocalDateTime.now());
    when(productDao.findById(101L)).thenReturn(Optional.of(p));
    when(cartDao.findByUser(1L)).thenReturn(List.of());

    cartService.add(1L, 101L, 2);

    verify(cartDao).add(1L, 101L, 2);
  }

  @Test
  void updatesItemQuantitySuccessfully() {
    Product p = new Product(101L, 2L, "Seller", "Silk Scarf", "Desc",
        new BigDecimal("499.00"), 10, "Accessories", null, LocalDateTime.now());
    when(productDao.findById(101L)).thenReturn(Optional.of(p));
    when(cartDao.update(1L, 101L, 3)).thenReturn(true);

    cartService.update(1L, 101L, 3);

    verify(cartDao).update(1L, 101L, 3);
  }

  @Test
  void removesItemSuccessfully() {
    when(cartDao.remove(1L, 101L)).thenReturn(true);

    cartService.remove(1L, 101L);

    verify(cartDao).remove(1L, 101L);
  }

  @Test
  void calculatesCartTotalCorrectly() {
    Product p1 = new Product(101L, 2L, "Seller", "Item 1", "Desc",
        new BigDecimal("200.00"), 10, "Apparel", null, LocalDateTime.now());
    Product p2 = new Product(102L, 2L, "Seller", "Item 2", "Desc",
        new BigDecimal("350.00"), 10, "Apparel", null, LocalDateTime.now());

    CartItem i1 = new CartItem(1L, 1L, p1, 2); // 400.00
    CartItem i2 = new CartItem(2L, 1L, p2, 1); // 350.00

    when(cartDao.findByUser(1L)).thenReturn(List.of(i1, i2));

    BigDecimal total = cartService.total(1L);
    assertEquals(new BigDecimal("750.00"), total);
  }
}
