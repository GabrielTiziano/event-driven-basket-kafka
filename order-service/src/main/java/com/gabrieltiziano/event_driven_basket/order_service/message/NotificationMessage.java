package com.gabrieltiziano.event_driven_basket.order_service.message;

import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import lombok.Builder;

@Builder
public record NotificationMessage(
        String orderId,
        String message,
        OrderEvent orderEvent
) {
}
