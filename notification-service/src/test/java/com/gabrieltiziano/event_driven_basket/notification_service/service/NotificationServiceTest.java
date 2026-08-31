package com.gabrieltiziano.event_driven_basket.notification_service.service;

import com.gabrieltiziano.event_driven_basket.notification_service.message.NotificationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> emailCaptor;

    private NotificationMessage buildMessage() {
        NotificationMessage message = new NotificationMessage();
        message.setOrderId("order-1");
        message.setMessage("Pedido criado com sucesso");
        message.setOrderEvent("CREATE");
        return message;
    }

    @Test
    void shouldSendEmailWhenHandlingMessage() {
        notificationService.handle(buildMessage());

        verify(mailSender).send(emailCaptor.capture());
        SimpleMailMessage sent = emailCaptor.getValue();
        assertThat(sent.getSubject()).contains("order-1");
        assertThat(sent.getText()).isEqualTo("Pedido criado com sucesso");
    }

    @Test
    void shouldNotThrowWhenEmailFails() {
        doThrow(new MailSendException("smtp indisponível"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // falha de e-mail NÃO pode propagar — senão o Kafka reprocessa em loop
        notificationService.handle(buildMessage());

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}