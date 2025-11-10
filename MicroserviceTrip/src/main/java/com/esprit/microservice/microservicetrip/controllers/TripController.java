package com.esprit.microservice.microservicetrip.controllers;

import com.esprit.microservice.microservicetrip.DTO.PaymentDTO;
import com.esprit.microservice.microservicetrip.entities.Trip;
import com.esprit.microservice.microservicetrip.services.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    // Récupérer tous les paiements via Trip
    @GetMapping("/payments")
    public List<PaymentDTO> getAllPayments() {
        return tripService.getAllPayments();
    }

    @GetMapping("/payments/{id}")
    public PaymentDTO getPaymentById(@PathVariable Integer id) {
        return tripService.getPaymentById(id);
    }
    
    //Trip
    @PostMapping("/createTrip")
    public ResponseEntity<?> createTrip(@RequestBody Trip trip,
                                        @RequestParam(required = false) String userId,
                                        @RequestParam(required = false) String driverId) {
        try {
            Trip savedTrip = tripService.createTrip(trip, userId, driverId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTrip);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du trajet : " + e.getMessage());
        }
    }
    @Value("${welcome.message}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }

    @PutMapping("/updateTrip/{tripId}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Integer tripId, @RequestBody Trip trip) {
        return ResponseEntity.ok(tripService.updateTrip(tripId, trip));
    }

    @DeleteMapping("/deleteTrip/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Integer tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getTrip/{tripId}")
    public ResponseEntity<Trip> getTripById(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripService.getTripById(tripId));
    }

    @GetMapping("/getAllTrips")
    public ResponseEntity<List<Trip>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping("/getTripsForUser")
    public ResponseEntity<List<Trip>> getTripsForUser(@RequestParam String userId) {
        return ResponseEntity.ok(tripService.getTripsForUser(userId));
    }

    @GetMapping("/getTripsForDriver")
    public ResponseEntity<List<Trip>> getTripsForDriver(@RequestParam String driverId) {
        return ResponseEntity.ok(tripService.getTripsForDriver(driverId));
    }

    @PutMapping("/acceptTrip/{tripId}")
    public ResponseEntity<Trip> acceptTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripService.acceptTrip(tripId));
    }

    @PutMapping("/refuseTrip/{tripId}")
    public ResponseEntity<Trip> refuseTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripService.refuseTrip(tripId));
    }

    @PutMapping("/completeTrip/{tripId}")
    public ResponseEntity<Trip> completeTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripService.completeTrip(tripId));
    }
}
