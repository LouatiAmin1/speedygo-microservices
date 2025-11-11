package com.esprit.microservice.microservicetrip.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.esprit.microservice.microservicetrip.config.RabbitMQConfig.*;

@Component
public class TripNotificationSender {

    private final RabbitTemplate rabbitTemplate;

    public TripNotificationSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTripNotification(String type, String message, String userKeycloakId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", type);
            payload.put("content", message);
            payload.put("userKeycloakId", userKeycloakId);

            String jsonMessage = new ObjectMapper().writeValueAsString(payload);

            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, jsonMessage);
            System.out.println("📤 Trip Notification sent to RabbitMQ: " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
