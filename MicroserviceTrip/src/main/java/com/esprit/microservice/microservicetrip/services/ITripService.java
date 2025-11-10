package com.esprit.microservice.microservicetrip.services;

import com.esprit.microservice.microservicetrip.entities.Trip;

public interface ITripService {
    Trip createTrip(Trip trip, String userKeycloakId, String driverKeycloakId);
    Trip updateTrip(Integer tripId, Trip updatedTrip);
    void deleteTrip(Integer tripId);
    Trip getTripById(Integer tripId);
}
