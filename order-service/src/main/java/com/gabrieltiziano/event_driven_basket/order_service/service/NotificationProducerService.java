package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.message.NotificationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducerService {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topic;

    public NotificationProducerService(KafkaTemplate<String, NotificationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(NotificationMessage message) {
        kafkaTemplate.send(topic, message.orderId(), message);
    }
}