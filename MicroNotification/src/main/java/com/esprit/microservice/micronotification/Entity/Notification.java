package com.esprit.microservice.micronotification.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @Column(length = 2000)
    private String notificationContent;

    private Date notificationDate;

    private String notificationStatus;

    // pour identifier l'utilisateur (remplace SimpleUser relation)
    private String userKeycloakId;
}
