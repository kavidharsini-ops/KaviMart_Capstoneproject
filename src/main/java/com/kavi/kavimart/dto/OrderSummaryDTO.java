package com.kavi.kavimart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
/** Order summary assembled with its immutable builder. */
public final class OrderSummaryDTO {
  private final long id,buyerId; private final String buyerName,status; private final BigDecimal totalAmount; private final LocalDateTime createdAt; private final List<OrderLineDTO> items;
  private OrderSummaryDTO(Builder b) { id=b.id;buyerId=b.buyerId;buyerName=b.buyerName;status=b.status;totalAmount=b.totalAmount;createdAt=b.createdAt;items=List.copyOf(b.items); }
  public long getId(){return id;} public long getBuyerId(){return buyerId;} public String getBuyerName(){return buyerName;} public String getStatus(){return status;} public BigDecimal getTotalAmount(){return totalAmount;} public LocalDateTime getCreatedAt(){return createdAt;} public List<OrderLineDTO> getItems(){return items;}
  public static Builder builder(){return new Builder();}
  public static final class Builder {
    private long id,buyerId; private String buyerName="",status="PENDING"; private BigDecimal totalAmount=BigDecimal.ZERO; private LocalDateTime createdAt; private List<OrderLineDTO> items=List.of();
    public Builder id(long value){id=value;return this;} public Builder buyerId(long value){buyerId=value;return this;} public Builder buyerName(String value){buyerName=value;return this;} public Builder status(String value){status=value;return this;} public Builder totalAmount(BigDecimal value){totalAmount=value;return this;} public Builder createdAt(LocalDateTime value){createdAt=value;return this;} public Builder items(List<OrderLineDTO> value){items=value;return this;} public OrderSummaryDTO build(){return new OrderSummaryDTO(this);}
  }
}
