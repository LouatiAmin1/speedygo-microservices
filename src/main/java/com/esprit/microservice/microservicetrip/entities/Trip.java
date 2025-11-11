package com.esprit.microservice.microservicetrip.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripId;

    private String tripDeparture;   // Point de départ
    private String tripDestination; // Destination
    private LocalDateTime tripDate; // Date et heure du trajet
    private String tripDuration;    // Durée du trajet
    private BigDecimal tripPrice;   // Prix du trajet

    @Enumerated(EnumType.STRING)
    private TripType tripType;      // EXPRESS_TRIP, LONG_DISTANCE_TRAVEL

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus = ReservationStatus.PENDING;

    private BigDecimal latitude;    // Latitude pour géolocalisation (optionnel)
    private BigDecimal longitude;   // Longitude pour géolocalisation (optionnel)

    @Column(nullable = false)
    private Boolean readyForDriverRating = false;

    @Column(nullable = false)
    private Boolean readyForPassengerRating = false;

    @Column(nullable = false)
    private Integer numberOfPassengers = 1;

    @Column(name = "is_rated")
    private Boolean isRated = false;

    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;

    // Identifiants simples pour utilisateur et conducteur
    private String userId;    // ID du passager
    private String driverId;  // ID du conducteur
}
