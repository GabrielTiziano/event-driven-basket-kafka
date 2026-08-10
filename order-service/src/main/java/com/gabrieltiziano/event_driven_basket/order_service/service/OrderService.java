package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.dto.request.CreateOrderRequest;
import com.gabrieltiziano.event_driven_basket.order_service.dto.response.OrderResponse;
import com.gabrieltiziano.event_driven_basket.order_service.entity.Order;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.PaymentMethod;
import com.gabrieltiziano.event_driven_basket.order_service.mapper.OrderMapper;
import com.gabrieltiziano.event_driven_basket.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStateService orderStateService;

    public OrderService(OrderRepository orderRepository, OrderStateService orderStateService) {
        this.orderRepository = orderRepository;
        this.orderStateService = orderStateService;
    }

    public OrderResponse createOrder(CreateOrderRequest orderRequest) {
        Order newOrder = OrderMapper.toEntity(orderRequest);

        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());

        return OrderMapper.toResponse(orderRepository.save(newOrder));
    }

    public OrderResponse payOrder(String id, PaymentMethod paymentMethod) {
        Order order = getOrder(id);
        OrderStatus newStatus = orderStateService.processEvent(order.getStatus(), OrderEvent.PAY);

        order.setStatus(newStatus);
        order.setPaymentMethod(paymentMethod);
        order.setUpdatedAt(LocalDateTime.now());

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse shipOrder(String id) {
        return transition(id, OrderEvent.SHIP);
    }

    public OrderResponse deliverOrder(String id) {
        return transition(id, OrderEvent.DELIVER);
    }

    public OrderResponse cancelOrder(String id) {
        return transition(id, OrderEvent.CANCEL);
    }

    private OrderResponse transition(String id, OrderEvent orderEvent) {
        Order order = getOrder(id);
        OrderStatus newStatus = orderStateService.processEvent(order.getStatus(), orderEvent);

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    //TODO: criar exception personalizada
    private Order getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
}
