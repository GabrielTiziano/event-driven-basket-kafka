package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import com.gabrieltiziano.event_driven_basket.order_service.message.NotificationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationProducerServiceTest {

    @Mock
    private KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @InjectMocks
    private NotificationProducerService producerService;

    @Test
    void shouldSendMessageUsingOrderIdAsKey() {
        ReflectionTestUtils.setField(producerService, "topic", "notification-topic");

        NotificationMessage message =
                new NotificationMessage("order-1", "Pedido criado com sucesso", OrderEvent.CREATE);

        producerService.sendMessage(message);

        verify(kafkaTemplate).send("notification-topic", "order-1", message);
    }
}