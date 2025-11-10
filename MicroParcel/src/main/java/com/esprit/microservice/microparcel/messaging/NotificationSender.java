package com.esprit.microservice.microparcel.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import static com.esprit.microservice.microparcel.config.RabbitMQConfig.*;

@Component
public class NotificationSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public NotificationSender(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendParcelShippedNotification(Integer parcelId, String userKeycloakId, String destination) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "PARCEL_SHIPPED");
            message.put("parcelId", parcelId);
            message.put("destination", destination);
            message.put("userKeycloakId", userKeycloakId);
            message.put("content", String.format("Your parcel to %s has been shipped!", destination));

            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, json);
            System.out.println("📦 Notification sent to RabbitMQ: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
