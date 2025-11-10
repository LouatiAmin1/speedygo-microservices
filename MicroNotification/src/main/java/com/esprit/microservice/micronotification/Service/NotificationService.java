package com.esprit.microservice.micronotification.Service;

import com.esprit.microservice.micronotification.Entity.Notification;
import com.esprit.microservice.micronotification.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // 🔔 Création locale d'une notification
    public Notification createNotification(String content, String userKeycloakId) {
        Notification notif = new Notification();
        notif.setNotificationContent(content);
        notif.setNotificationDate(new Date());
        notif.setNotificationStatus("PENDING");
        notif.setUserKeycloakId(userKeycloakId);
        return notificationRepository.save(notif);
    }

    // Récupération des notifications d’un utilisateur
    public List<Notification> getAllNotificationsForUser(String userKeycloakId) {
        return notificationRepository.findByUserKeycloakIdOrderByNotificationDateDesc(userKeycloakId);
    }
}
