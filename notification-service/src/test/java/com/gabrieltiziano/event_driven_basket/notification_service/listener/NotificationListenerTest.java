package com.gabrieltiziano.event_driven_basket.notification_service.listener;

import com.gabrieltiziano.event_driven_basket.notification_service.message.NotificationMessage;
import com.gabrieltiziano.event_driven_basket.notification_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationListener notificationListener;

    @Test
    void shouldDelegateMessageToService() {
        NotificationMessage message = new NotificationMessage();
        message.setOrderId("order-1");
        message.setMessage("Pedido criado");
        message.setOrderEvent("CREATE");

        notificationListener.listener(message);

        verify(notificationService).handle(message);
    }
}