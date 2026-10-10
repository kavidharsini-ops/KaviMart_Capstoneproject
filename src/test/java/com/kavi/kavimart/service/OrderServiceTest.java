package com.kavi.kavimart.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.dao.OrderDao;
import com.kavi.kavimart.dto.OrderLineDTO;
import com.kavi.kavimart.dto.OrderSummaryDTO;
import com.kavi.kavimart.exception.ConflictException;
import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.service.payment.PaymentStrategy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  OrderDao dao;

  @Mock
  CartService carts;

  @Mock
  PaymentStrategy payment;

  @InjectMocks
  OrderService service;

  @Test
  void invalidBuyerIsRejectedBeforeDaoCalls() {
    assertThrows(ValidationException.class, () -> service.placeOrder(0));
    verifyNoInteractions(dao, carts, payment);
  }

  @Test
  void sellerCannotSkipOrderStatus() {
    OrderSummaryDTO order = OrderSummaryDTO.builder()
        .id(8)
        .status("PENDING")
        .items(List.of(new OrderLineDTO(1, "item", 1, BigDecimal.ONE, 2)))
        .build();

    when(dao.findById(8)).thenReturn(Optional.of(order));

    ConflictException e = assertThrows(ConflictException.class, () ->
        service.updateSellerStatus(2, 8, "SHIPPED"));
    assertEquals(409, e.getStatus());
    verify(dao, never()).transitionSeller(anyLong(), anyLong(), anyString(), anyString());
  }

  @Test
  void sellerStatusAdvancesOneStep() {
    OrderSummaryDTO order = OrderSummaryDTO.builder()
        .id(9)
        .status("PENDING")
        .items(List.of(new OrderLineDTO(1, "item", 1, BigDecimal.ONE, 2)))
        .build();

    when(dao.findById(9)).thenReturn(Optional.of(order));
    when(dao.transitionSeller(9, 2, "PENDING", "CONFIRMED")).thenReturn(true);

    assertDoesNotThrow(() -> service.updateSellerStatus(2, 9, "CONFIRMED"));
  }
}
