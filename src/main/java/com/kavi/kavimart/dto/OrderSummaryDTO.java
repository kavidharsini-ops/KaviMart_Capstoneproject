package com.kavi.kavimart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order summary assembled with its immutable builder.
 */
public final class OrderSummaryDTO {
  private final long id;
  private final long buyerId;
  private final String buyerName;
  private final String status;
  private final BigDecimal totalAmount;
  private final LocalDateTime createdAt;
  private final List<OrderLineDTO> items;

  private OrderSummaryDTO(Builder b) {
    this.id = b.id;
    this.buyerId = b.buyerId;
    this.buyerName = b.buyerName;
    this.status = b.status;
    this.totalAmount = b.totalAmount;
    this.createdAt = b.createdAt;
    this.items = b.items != null ? List.copyOf(b.items) : List.of();
  }

  public long getId() {
    return id;
  }

  public long getBuyerId() {
    return buyerId;
  }

  public String getBuyerName() {
    return buyerName;
  }

  public String getStatus() {
    return status;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<OrderLineDTO> getItems() {
    return items;
  }

  /**
   * Returns a new Builder instance.
   *
   * @return Builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder for OrderSummaryDTO.
   */
  public static final class Builder {
    private long id;
    private long buyerId;
    private String buyerName = "";
    private String status = "PENDING";
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private List<OrderLineDTO> items = List.of();

    public Builder id(long value) {
      this.id = value;
      return this;
    }

    public Builder buyerId(long value) {
      this.buyerId = value;
      return this;
    }

    public Builder buyerName(String value) {
      this.buyerName = value;
      return this;
    }

    public Builder status(String value) {
      this.status = value;
      return this;
    }

    public Builder totalAmount(BigDecimal value) {
      this.totalAmount = value;
      return this;
    }

    public Builder createdAt(LocalDateTime value) {
      this.createdAt = value;
      return this;
    }

    public Builder items(List<OrderLineDTO> value) {
      this.items = value;
      return this;
    }

    public OrderSummaryDTO build() {
      return new OrderSummaryDTO(this);
    }
  }
}
