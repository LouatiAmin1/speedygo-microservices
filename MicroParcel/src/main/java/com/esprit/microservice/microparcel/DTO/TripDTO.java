package com.esprit.microservice.microparcel.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class TripDTO {
        private Integer tripId;
        private String tripDeparture;
        private String tripDestination;
        private LocalDateTime tripDate;
        private String tripDuration;
        private BigDecimal tripPrice;
        private String tripType; // EXPRESS_TRIP, LONG_DISTANCE_TRAVEL
        private String reservationStatus; // PENDING, CONFIRMED, ...
        private String userId;
        private String driverId;
    }


