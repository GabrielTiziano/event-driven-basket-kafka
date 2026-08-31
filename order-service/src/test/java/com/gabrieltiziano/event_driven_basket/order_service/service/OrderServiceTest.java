package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.dto.request.CreateOrderRequest;
import com.gabrieltiziano.event_driven_basket.order_service.dto.response.OrderResponse;
import com.gabrieltiziano.event_driven_basket.order_service.entity.Order;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.PaymentMethod;
import com.gabrieltiziano.event_driven_basket.order_service.exception.InvalidOrderTransitionException;
import com.gabrieltiziano.event_driven_basket.order_service.exception.OrderNotFoundException;
import com.gabrieltiziano.event_driven_basket.order_service.message.NotificationMessage;
import com.gabrieltiziano.event_driven_basket.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStateService orderStateService;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Mock
    private NotificationProducerService notificationProducerService;

    @Captor
    private ArgumentCaptor<NotificationMessage> notificationCaptor;

    @Test
    void shouldCreateOrderWithStatusCreated() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cli-1", "bsk-9",
                new BigDecimal("100.00"), new BigDecimal("10.00"));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    o.setId("order-123");
                    return o;
                });

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.id()).isEqualTo("order-123");
        assertThat(response.totalAmount()).isEqualByComparingTo("110.00");
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);

        verify(orderRepository).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldProcessPaymentAndSetPaymentMethod() {
        Order order = buildOrder("order-1", OrderStatus.CREATED);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderStateService.processEvent(OrderStatus.CREATED, OrderEvent.PAY))
                .thenReturn(OrderStatus.PAID);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.payOrder("order-1", PaymentMethod.PIX);

        assertThat(response.status()).isEqualTo(OrderStatus.PAID);
        assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.PIX);

        verify(orderStateService).processEvent(OrderStatus.CREATED, OrderEvent.PAY);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldShipOrder() {
        Order order = buildOrder("order-1", OrderStatus.PAID);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderStateService.processEvent(OrderStatus.PAID, OrderEvent.SHIP))
                .thenReturn(OrderStatus.SHIPPED);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.shipOrder("order-1");

        assertThat(response.status()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderStateService).processEvent(OrderStatus.PAID, OrderEvent.SHIP);
    }

    @Test
    void shouldDeliverOrder() {
        Order order = buildOrder("order-1", OrderStatus.SHIPPED);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderStateService.processEvent(OrderStatus.SHIPPED, OrderEvent.DELIVER))
                .thenReturn(OrderStatus.DELIVERED);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.deliverOrder("order-1");

        assertThat(response.status()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void shouldCancelOrder() {
        Order order = buildOrder("order-1", OrderStatus.CREATED);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderStateService.processEvent(OrderStatus.CREATED, OrderEvent.CANCEL))
                .thenReturn(OrderStatus.CANCELLED);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.cancelOrder("order-1");

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.shipOrder("non-existent"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Pedido não encontrado");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveWhenTransitionIsInvalid() {
        Order order = buildOrder("order-1", OrderStatus.CREATED);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderStateService.processEvent(OrderStatus.CREATED, OrderEvent.DELIVER))
                .thenThrow(new InvalidOrderTransitionException("Invalid transition"));

        assertThatThrownBy(() -> orderService.deliverOrder("order-1"))
                .isInstanceOf(InvalidOrderTransitionException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldPublishNotificationWhenOrderIsCreated() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cli-1", "bsk-9", new BigDecimal("100.00"), new BigDecimal("10.00"));

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId("order-123");
            return o;
        });

        orderService.createOrder(request);

        verify(notificationProducerService).sendMessage(notificationCaptor.capture());
        NotificationMessage sent = notificationCaptor.getValue();
        assertThat(sent.orderId()).isEqualTo("order-123");
        assertThat(sent.orderEvent()).isEqualTo(OrderEvent.CREATE);
    }

    private Order buildOrder(String id, OrderStatus status) {
        return Order.builder()
                .id(id)
                .customerId("cli-1")
                .basketId("bsk-9")
                .itemsAmount(new BigDecimal("100.00"))
                .shippingCost(new BigDecimal("10.00"))
                .status(status)
                .build();
    }
}