package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.dto.request.CreateOrderRequest;
import com.gabrieltiziano.event_driven_basket.order_service.dto.response.OrderResponse;
import com.gabrieltiziano.event_driven_basket.order_service.entity.Order;
import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderStatus;
import com.gabrieltiziano.event_driven_basket.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    void deveCriarPedidoComStatusCreated() {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                "cli-1", "bsk-9",
                new BigDecimal("100.00"), new BigDecimal("10.00"));

        // simula o Mongo devolvendo o pedido salvo com id gerado
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    o.setId("order-123");
                    return o;
                });

        // when
        OrderResponse response = orderService.createOrder(request);

        // then — o que voltou pro cliente
        assertThat(response.id()).isEqualTo("order-123");
        assertThat(response.customerId()).isEqualTo("cli-1");
        assertThat(response.basketId()).isEqualTo("bsk-9");
        assertThat(response.totalAmount()).isEqualByComparingTo("110.00");
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);

        // then — o que foi persistido
        verify(orderRepository).save(orderCaptor.capture());
        Order salvo = orderCaptor.getValue();
        assertThat(salvo.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(salvo.getCreatedAt()).isNotNull();
        assertThat(salvo.getUpdatedAt()).isNotNull();
    }
}