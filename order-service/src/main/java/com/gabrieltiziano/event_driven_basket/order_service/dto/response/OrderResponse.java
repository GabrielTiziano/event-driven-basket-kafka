package com.gabrieltiziano.event_driven_basket.order_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.PaymentMethod;

import java.math.BigDecimal;

public record OrderResponse(
        String id,
        String customerId,
        String basketId,
        BigDecimal totalAmount,
        OrderStatus status,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        PaymentMethod paymentMethod
) {
}
