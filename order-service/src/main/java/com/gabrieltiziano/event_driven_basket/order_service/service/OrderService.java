package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.dto.request.CreateOrderRequest;
import com.gabrieltiziano.event_driven_basket.order_service.dto.response.OrderResponse;
import com.gabrieltiziano.event_driven_basket.order_service.entity.Order;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.PaymentMethod;
import com.gabrieltiziano.event_driven_basket.order_service.mapper.OrderMapper;
import com.gabrieltiziano.event_driven_basket.order_service.message.NotificationMessage;
import com.gabrieltiziano.event_driven_basket.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderStateService orderStateService;
    private final NotificationProducerService notificationProducerService;

    public OrderService(OrderRepository orderRepository, OrderStateService orderStateService, NotificationProducerService notificationProducerService) {
        this.orderRepository = orderRepository;
        this.orderStateService = orderStateService;
        this.notificationProducerService = notificationProducerService;
    }

    public OrderResponse createOrder(CreateOrderRequest orderRequest) {
        Order newOrder = OrderMapper.toEntity(orderRequest);

        newOrder.setStatus(OrderStatus.CREATED);
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(newOrder);
        notify(saved, OrderEvent.CREATE);

        return OrderMapper.toResponse(saved);
    }

    public OrderResponse payOrder(String id, PaymentMethod paymentMethod) {
        Order order = getOrder(id);
        OrderStatus newStatus = orderStateService.processEvent(order.getStatus(), OrderEvent.PAY);

        order.setStatus(newStatus);
        order.setPaymentMethod(paymentMethod);
        order.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        notify(saved, OrderEvent.PAY);

        return OrderMapper.toResponse(saved);
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

        Order saved = orderRepository.save(order);
        notify(saved, orderEvent);
        return OrderMapper.toResponse(saved);
    }

    //TODO: criar exception personalizada
    private Order getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    private void notify(Order order, OrderEvent event) {
        NotificationMessage message = new NotificationMessage(
                order.getId(),
                messageFor(event),
                event
        );
        notificationProducerService.sendMessage(message);
    }
    private String messageFor(OrderEvent event) {
        return switch (event) {
            case CREATE  -> "Pedido criado com sucesso";
            case PAY     -> "Pagamento confirmado";
            case SHIP    -> "Pedido enviado";
            case DELIVER -> "Pedido entregue";
            case CANCEL  -> "Pedido cancelado";
        };
    }

}
