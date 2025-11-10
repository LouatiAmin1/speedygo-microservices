package com.esprit.microservice.micronotification.Repository;

import com.esprit.microservice.micronotification.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserKeycloakIdOrderByNotificationDateDesc(String userKeycloakId);
}
