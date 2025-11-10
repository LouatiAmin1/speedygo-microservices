package com.esprit.microservice.microservicetrip.services;

import com.esprit.microservice.microservicetrip.DTO.PaymentDTO;
import com.esprit.microservice.microservicetrip.clients.FinancialClient;
import com.esprit.microservice.microservicetrip.entities.ReservationStatus;
import com.esprit.microservice.microservicetrip.entities.Trip;
import com.esprit.microservice.microservicetrip.messaging.TripNotificationSender;
import com.esprit.microservice.microservicetrip.repositories.TripRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripNotificationSender notificationSender;
    @Autowired
    private FinancialClient financialClient;

    public PaymentDTO getPaymentById(Integer id) {
        // Appel correct
        return financialClient.getPaymentById(id);
    }

    public List<PaymentDTO> getAllPayments() {
        return financialClient.getAllPayments();
    }


    // ---------- CREATE ----------
    public Trip createTrip(Trip trip, String userId, String driverId) {

        if (trip == null)
            throw new IllegalArgumentException("Le trajet ne peut pas être nul.");

        if (trip.getTripDate() == null || trip.getTripDate().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("La date du trajet est invalide ou déjà passée.");

        if (trip.getNumberOfPassengers() == null || trip.getNumberOfPassengers() < 1 || trip.getNumberOfPassengers() > 4)
            throw new IllegalArgumentException("Le nombre de passagers doit être entre 1 et 4.");

        if (trip.getTripDeparture() == null || trip.getTripDeparture().isBlank())
            throw new IllegalArgumentException("Le point de départ est obligatoire.");

        if (trip.getTripDestination() == null || trip.getTripDestination().isBlank())
            throw new IllegalArgumentException("La destination est obligatoire.");

        // ✅ Valeurs par défaut si userId ou driverId non fournis
        trip.setUserId((userId == null || userId.isBlank()) ? "TEMP_USER" : userId);
        trip.setDriverId((driverId == null || driverId.isBlank()) ? "TEMP_DRIVER" : driverId);

        // ✅ Statut par défaut
        if (trip.getReservationStatus() == null)
            trip.setReservationStatus(ReservationStatus.PENDING);

        Trip savedTrip = tripRepository.save(trip);

        // ✅ Notification asynchrone (si RabbitMQ configuré)
        if (notificationSender != null) {
            notificationSender.sendTripNotification(
                    "TRIP_CREATED",
                    "Le trajet de " + trip.getTripDeparture() + " à " + trip.getTripDestination() + " a été créé.",
                    trip.getUserId()
            );
        }

        return savedTrip;
    }
    // ---------- ACCEPT ----------
    public Trip acceptTrip(Integer tripId) {
        Trip trip = getTripById(tripId);
        trip.setReservationStatus(ReservationStatus.CONFIRMED);
        Trip saved = tripRepository.save(trip);

        // ✅ Notification asynchrone
        notificationSender.sendTripNotification(
                "TRIP_ACCEPTED",
                "Votre trajet a été accepté par le conducteur.",
                trip.getUserId()
        );

        return saved;
    }

    // ---------- REFUSE ----------
    public Trip refuseTrip(Integer tripId) {
        Trip trip = getTripById(tripId);
        trip.setReservationStatus(ReservationStatus.CANCELED);
        Trip saved = tripRepository.save(trip);

        // ✅ Notification asynchrone
        notificationSender.sendTripNotification(
                "TRIP_REFUSED",
                "Votre demande de trajet a été refusée.",
                trip.getUserId()
        );

        return saved;
    }

    // ---------- READ ----------
    public Trip getTripById(Integer tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public List<Trip> getTripsForUser(String userId) {
        return tripRepository.findByUserId(userId);
    }

    public List<Trip> getTripsForDriver(String driverId) {
        return tripRepository.findByDriverId(driverId);
    }

    // ---------- UPDATE ----------
    public Trip updateTrip(Integer tripId, Trip updatedTrip) {
        Trip existing = getTripById(tripId);

        existing.setTripDate(updatedTrip.getTripDate());
        existing.setTripDeparture(updatedTrip.getTripDeparture());
        existing.setTripDestination(updatedTrip.getTripDestination());
        existing.setNumberOfPassengers(updatedTrip.getNumberOfPassengers());
        existing.setTripDuration(updatedTrip.getTripDuration());
        existing.setTripPrice(updatedTrip.getTripPrice());
        existing.setTripType(updatedTrip.getTripType());

        return tripRepository.save(existing);
    }


    public Trip completeTrip(Integer tripId) {
        Trip trip = getTripById(tripId);

        if (trip.getReservationStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Le trajet doit être CONFIRMED pour être complété.");
        }

        trip.setReservationStatus(ReservationStatus.COMPLETED);
        trip.setReadyForDriverRating(true);
        trip.setReadyForPassengerRating(true);
        return tripRepository.save(trip);
    }

    // ---------- DELETE ----------
    public void deleteTrip(Integer tripId) {
        tripRepository.deleteById(tripId);
    }

    // ---------- Scheduled Reminders ----------
    @Transactional
    @Scheduled(fixedRate = 60000) // toutes les minutes
    public void sendTripReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteenMinutesLater = now.plusMinutes(15);

        List<Trip> upcomingTrips = tripRepository.findByTripDateBetweenAndReminderSentFalse(now, fifteenMinutesLater);

        for (Trip trip : upcomingTrips) {
            log.info("Rappel: Trajet de {} à {} prévu à {}", trip.getTripDeparture(), trip.getTripDestination(), trip.getTripDate());
            trip.setReminderSent(true);
            tripRepository.save(trip);
        }
    }
}
