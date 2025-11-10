package com.esprit.microservice.micronotification.messaging;

import com.esprit.microservice.micronotification.Entity.Notification;
import com.esprit.microservice.micronotification.Repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class TripNotificationListener {

    private final NotificationRepository notificationRepository;

    public TripNotificationListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @RabbitListener(queues = "trip_notification_queue")
    public void handleTripNotification(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(message, Map.class);

            String content = (String) map.get("content");
            String userKeycloakId = (String) map.get("userKeycloakId");

            Notification notif = new Notification();
            notif.setNotificationContent(content);
            notif.setNotificationDate(new Date());
            notif.setNotificationStatus("RECEIVED");
            notif.setUserKeycloakId(userKeycloakId);

            notificationRepository.save(notif);
            System.out.println("🚖 Trip Notification received and saved: " + notif.getNotificationContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
