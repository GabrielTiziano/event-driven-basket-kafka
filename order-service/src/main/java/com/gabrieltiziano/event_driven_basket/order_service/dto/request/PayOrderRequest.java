package com.gabrieltiziano.event_driven_basket.order_service.dto.request;

import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PayOrderRequest(
        @NotNull
        PaymentMethod paymentMethod
) {
}
