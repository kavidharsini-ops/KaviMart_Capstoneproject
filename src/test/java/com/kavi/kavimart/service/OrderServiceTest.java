package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.OrderDao;import com.kavi.kavimart.dto.OrderSummaryDTO;import com.kavi.kavimart.exception.*;import com.kavi.kavimart.service.payment.PaymentStrategy;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.InjectMocks;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;import java.util.Optional;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class) class OrderServiceTest {
 @Mock OrderDao dao;@Mock CartService carts;@Mock PaymentStrategy payment;@InjectMocks OrderService service;
 @Test void invalidBuyerIsRejectedBeforeDaoCalls(){assertThrows(ValidationException.class,()->service.placeOrder(0));verifyNoInteractions(dao,carts,payment);}
 @Test void sellerCannotSkipOrderStatus(){when(dao.findById(8)).thenReturn(Optional.of(OrderSummaryDTO.builder().id(8).status("PENDING").items(java.util.List.of(new com.kavi.kavimart.dto.OrderLineDTO(1,"item",1,java.math.BigDecimal.ONE,2))).build()));ConflictException e=assertThrows(ConflictException.class,()->service.updateSellerStatus(2,8,"SHIPPED"));assertEquals(409,e.getStatus());verify(dao,never()).transitionSeller(anyLong(),anyLong(),anyString(),anyString());}
 @Test void sellerStatusAdvancesOneStep(){when(dao.findById(9)).thenReturn(Optional.of(OrderSummaryDTO.builder().id(9).status("PENDING").items(java.util.List.of(new com.kavi.kavimart.dto.OrderLineDTO(1,"item",1,java.math.BigDecimal.ONE,2))).build()));when(dao.transitionSeller(9,2,"PENDING","CONFIRMED")).thenReturn(true);assertDoesNotThrow(()->service.updateSellerStatus(2,9,"CONFIRMED"));}
}
