package com.gabrieltiziano.event_driven_basket.notification_service.listener;

import com.gabrieltiziano.event_driven_basket.notification_service.message.NotificationMessage;
import com.gabrieltiziano.event_driven_basket.notification_service.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationListener {
    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group}")
    public void listener(NotificationMessage message) {
        log.info("Evento recebido | {}", message);
        notificationService.handle(message);
    }
}
