package com.gabrieltiziano.event_driven_basket.notification_service.service;

import com.gabrieltiziano.event_driven_basket.notification_service.message.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void handle(NotificationMessage message) {
        log.info("Processando notificação | orderId={} evento={}",
                message.getOrderId(), message.getOrderEvent());

        sendEmail(message);
    }

    private void sendEmail(NotificationMessage message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo("gghiaronitiziano@gmail.com");
            email.setSubject("Atualização do pedido " + message.getOrderId());
            email.setText(message.getMessage());

            mailSender.send(email);

            log.info("E-mail enviado | orderId={} evento={}",
                    message.getOrderId(), message.getOrderEvent());
        } catch (MailException e) {
            log.error("Falha ao enviar e-mail: | orderId={} evento={}", message.getOrderId(), message.getOrderEvent(), e);
        }
    }
}
