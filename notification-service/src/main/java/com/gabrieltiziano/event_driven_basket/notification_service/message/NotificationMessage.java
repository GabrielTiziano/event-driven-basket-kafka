package com.gabrieltiziano.event_driven_basket.notification_service.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NotificationMessage {
    private String orderId;
    private String message;
    private String orderEvent;
}
