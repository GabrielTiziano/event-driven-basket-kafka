package com.gabrieltiziano.event_driven_basket.order_service.service;

import com.gabrieltiziano.event_driven_basket.order_service.entity.enums.OrderEvent;
import com.gabrieltiziano.event_driven_basket.order_service.message.NotificationMessage;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "${kafka.topic}")
@TestPropertySource(properties = "kafka.url=${spring.embedded.kafka.brokers}")
class NotificationProducerServiceIntegrationTest {

    @Autowired
    private NotificationProducerService producer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Value("${kafka.topic}")
    private String topic;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldPublishNotificationMessageToKafka() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationMessage.class.getName());
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        try (Consumer<String, NotificationMessage> consumer =
                     new DefaultKafkaConsumerFactory<String, NotificationMessage>(props).createConsumer()) {

            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, topic);

            producer.sendMessage(
                    new NotificationMessage("order-1", "Pedido criado com sucesso", OrderEvent.CREATE));

            ConsumerRecord<String, NotificationMessage> record =
                    KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(10));

            assertThat(record.key()).isEqualTo("order-1");
            assertThat(record.value().orderId()).isEqualTo("order-1");
            assertThat(record.value().orderEvent()).isEqualTo(OrderEvent.CREATE);
        }
    }
}