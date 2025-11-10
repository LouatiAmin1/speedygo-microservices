package com.esprit.microservice.microparcel.controllers;

import com.esprit.microservice.microparcel.DTO.TripDTO;
import com.esprit.microservice.microparcel.entities.Parcel;
import com.esprit.microservice.microparcel.services.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/parcels")
public class ParcelController {

  @Autowired
  private ParcelService parcelService;
  @GetMapping("/trip/{tripId}")
  public ResponseEntity<TripDTO> getTrip(@PathVariable Integer tripId) {
    return ResponseEntity.ok(parcelService.getTripById(tripId));
  }

  @GetMapping("/trip")
  public ResponseEntity<List<TripDTO>> getAllTrips() {
    return ResponseEntity.ok(parcelService.getAllTrips());
  }
  @Value("${welcome.message}")
  private String welcomeMessage;

  @GetMapping("/welcome")
  public String welcome() {
    return welcomeMessage;
  }

  // CRUD
  @PostMapping
  public ResponseEntity<Parcel> createParcel(@RequestBody Parcel parcel) {
    return ResponseEntity.ok(parcelService.createParcel(parcel));
  }

  @GetMapping
  public ResponseEntity<List<Parcel>> getAllParcels() {
    return ResponseEntity.ok(parcelService.getAllParcels());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Parcel> getParcel(@PathVariable Integer id) {
    return ResponseEntity.ok(parcelService.getParcelById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Parcel> updateParcel(@PathVariable Integer id, @RequestBody Parcel parcel) {
    return ResponseEntity.ok(parcelService.updateParcel(id, parcel));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteParcel(@PathVariable Integer id) {
    parcelService.deleteParcel(id);
    return ResponseEntity.noContent().build();
  }

  // Mark shipped / delivered
  @PutMapping("/{id}/shipped")
  public ResponseEntity<Parcel> markShipped(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(parcelService.markAsShipped(id));
  }

  @PutMapping("/{id}/delivered")
  public ResponseEntity<Parcel> markDelivered(@PathVariable Integer id) throws Exception {
    return ResponseEntity.ok(parcelService.markAsDelivered(id));
  }

  // Upload damage image
  @PostMapping("/{id}/damage")
  public ResponseEntity<String> uploadDamageImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file, @RequestParam("desc") String desc) throws Exception {
    return ResponseEntity.ok(parcelService.saveDamageImage(id, file, desc));
  }

  // Stats
  @GetMapping("/stats/total")
  public ResponseEntity<Long> getTotal() {
    return ResponseEntity.ok(parcelService.getTotalParcels());
  }

  @GetMapping("/stats/damaged-percentage")
  public ResponseEntity<Double> getDamagedPercentage() {
    return ResponseEntity.ok(parcelService.getDamagedParcelsPercentage());
  }
}
