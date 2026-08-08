package com.gabrieltiziano.event_driven_basket.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank
        String customerId,

        @NotBlank
        String basketId,

        @NotNull @Positive
        BigDecimal itemsAmount,

        @NotNull @PositiveOrZero
        BigDecimal shippingCost
) {
}
