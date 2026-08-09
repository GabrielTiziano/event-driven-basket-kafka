package com.gabrieltiziano.event_driven_basket.order_service.mapper;

import com.gabrieltiziano.event_driven_basket.order_service.dto.request.CreateOrderRequest;
import com.gabrieltiziano.event_driven_basket.order_service.dto.response.OrderResponse;
import com.gabrieltiziano.event_driven_basket.order_service.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public static Order toEntity(CreateOrderRequest request) {
        return Order.builder()
                .customerId(request.customerId())
                .basketId(request.basketId())
                .itemsAmount(request.itemsAmount())
                .shippingCost(request.shippingCost())
                .build();
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getBasketId(),
                order.totalAmount(),
                order.getStatus(),
                order.getPaymentMethod()
        );
    }
}
