package com.esprit.microservice.microservicetrip.repositories;

import com.esprit.microservice.microservicetrip.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Integer> {

    List<Trip> findByUserId(String userId);

    List<Trip> findByDriverId(String driverId);

    @Query("SELECT t FROM Trip t WHERE t.tripDate BETWEEN :startDate AND :endDate AND t.reminderSent = false")
    List<Trip> findByTripDateBetweenAndReminderSentFalse(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
}
