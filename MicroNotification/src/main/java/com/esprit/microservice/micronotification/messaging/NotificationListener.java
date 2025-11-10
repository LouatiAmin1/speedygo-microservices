package com.esprit.microservice.micronotification.messaging;

import com.esprit.microservice.micronotification.Entity.Notification;
import com.esprit.microservice.micronotification.Repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;

@Component
public class NotificationListener {

    private final NotificationRepository notificationRepository;

    public NotificationListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // 🔔 Listener unique pour RabbitMQ
    @RabbitListener(queues = "notification_queue")
    public void handleNotification(String messageJson) {
        try {
            // 🔄 Désérialiser la chaîne JSON reçue
            Map<String, Object> message = new ObjectMapper().readValue(messageJson, Map.class);

            String content = (String) message.get("content");
            String userKeycloakId = (String) message.get("userKeycloakId");

            Notification notif = new Notification();
            notif.setNotificationContent(content);
            notif.setNotificationDate(new Date());
            notif.setNotificationStatus("RECEIVED");
            notif.setUserKeycloakId(userKeycloakId);

            notificationRepository.save(notif);
            System.out.println("✅ Notification saved: " + notif.getNotificationContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
