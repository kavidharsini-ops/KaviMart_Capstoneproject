package com.kavi.kavimart.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderSummaryDTOTest {

  @Test
  void testOrderSummaryBuilderPattern() {
    LocalDateTime now = LocalDateTime.now();
    OrderLineDTO item = new OrderLineDTO(101L, "Kurti", 2, new BigDecimal("899.00"), 2L);

    OrderSummaryDTO dto = OrderSummaryDTO.builder()
        .id(1L)
        .buyerId(4L)
        .buyerName("Mira Patel")
        .status("PENDING")
        .totalAmount(new BigDecimal("1798.00"))
        .createdAt(now)
        .items(List.of(item))
        .build();

    assertNotNull(dto);
    assertEquals(1L, dto.getId());
    assertEquals(4L, dto.getBuyerId());
    assertEquals("Mira Patel", dto.getBuyerName());
    assertEquals("PENDING", dto.getStatus());
    assertEquals(new BigDecimal("1798.00"), dto.getTotalAmount());
    assertEquals(now, dto.getCreatedAt());
    assertEquals(1, dto.getItems().size());
    assertEquals("Kurti", dto.getItems().get(0).getProductName());
  }
}
