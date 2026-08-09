package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.dto.request.CreateOrderRequest;
import com.gabrieltiziano.event_driven_basket.order_service.dto.response.OrderResponse;
import com.gabrieltiziano.event_driven_basket.order_service.entity.Order;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import com.gabrieltiziano.event_driven_basket.order_service.mapper.OrderMapper;
import com.gabrieltiziano.event_driven_basket.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest orderRequest) {
        Order newOrder = OrderMapper.toEntity(orderRequest);

        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setCreatedAt(java.time.LocalDateTime.now());
        newOrder.setUpdatedAt(java.time.LocalDateTime.now());

        return OrderMapper.toResponse(orderRepository.save(newOrder));
    }


}
