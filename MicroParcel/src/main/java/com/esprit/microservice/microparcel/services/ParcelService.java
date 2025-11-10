package com.esprit.microservice.microparcel.services;

import com.esprit.microservice.microparcel.DTO.TripDTO;
import com.esprit.microservice.microparcel.clients.TripClient;
import com.esprit.microservice.microparcel.entities.Parcel;
import com.esprit.microservice.microparcel.entities.Status;
import com.esprit.microservice.microparcel.messaging.NotificationSender;
import com.esprit.microservice.microparcel.repositories.ParcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;

@Service
public class ParcelService {

  @Autowired
  private ParcelRepository parcelRepository;

  @Autowired
  private NotificationSender notificationSender;
@Autowired
  private  TripClient tripClient;

  public TripDTO getTripById(Integer tripId) {
    return tripClient.getTripById(tripId);
  }

  public List<TripDTO> getAllTrips() {
    return tripClient.getAllTrips();
  }
  // ---------- CREATE ----------
  public Parcel createParcel(Parcel parcel) {
    parcel.setParcelDate(new Date());
    parcel.setStatus(Status.PENDING);
    parcel.setParcelPrice(determineParcelPrice(parcel.getParcelWeight()));
    return parcelRepository.save(parcel);
  }

  // ---------- READ ----------
  public List<Parcel> getAllParcels() {
    return parcelRepository.findAll();
  }

  public Parcel getParcelById(Integer id) {
    return parcelRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parcel not found with id: " + id));
  }

  // ---------- UPDATE ----------
  public Parcel updateParcel(Integer id, Parcel parcelDetails) {
    Parcel parcel = getParcelById(id);

    parcel.setParcelCategory(parcelDetails.getParcelCategory());
    parcel.setRecepeientPhoneNumber(parcelDetails.getRecepeientPhoneNumber());
    parcel.setSenderPhoneNumber(parcelDetails.getSenderPhoneNumber());
    parcel.setParcelDeparture(parcelDetails.getParcelDeparture());
    parcel.setParcelDestination(parcelDetails.getParcelDestination());
    parcel.setParcelWeight(parcelDetails.getParcelWeight());
    parcel.setParcelDate(parcelDetails.getParcelDate() != null ? parcelDetails.getParcelDate() : parcel.getParcelDate());
    parcel.setParcelPrice(parcelDetails.getParcelPrice() != null ? parcelDetails.getParcelPrice() : determineParcelPrice(parcel.getParcelWeight()));
    parcel.setStatus(parcelDetails.getStatus());
    parcel.setArchived(parcelDetails.isArchived());
    parcel.setDamageImageUrl(parcelDetails.getDamageImageUrl());
    parcel.setDamageDescription(parcelDetails.getDamageDescription());
    parcel.setDamageReportedAt(parcelDetails.getDamageReportedAt());

    return parcelRepository.save(parcel);
  }

  // ---------- DELETE ----------
  public void deleteParcel(Integer id) {
    Parcel parcel = getParcelById(id);
    parcelRepository.delete(parcel);
  }

  // ---------- Price estimation ----------
  public float determineParcelPrice(Double weight) {
    if (weight == null) return 10.0F;
    if (weight > 20) return 30.0F;
    else if (weight >= 5) return 20.0F;
    else return 10.0F;
  }

  // ---------- Mark shipped / delivered ----------
  public Parcel markAsShipped(Integer parcelId) throws Exception {
    Parcel parcel = getParcelById(parcelId);
    parcel.setStatus(Status.SHIPPED);
    Parcel updated = parcelRepository.save(parcel);

    // Envoi de l'événement asynchrone
    notificationSender.sendParcelShippedNotification(
            updated.getParcelId(),
            null, // on ignore Keycloak
            updated.getParcelDestination()
    );

    return updated;
  }

  public Parcel markAsDelivered(Integer parcelId) throws Exception {
    Parcel parcel = getParcelById(parcelId);
    parcel.setStatus(Status.DELIVERED);
    return parcelRepository.save(parcel);
  }

  // ---------- Damage image ----------
  public String saveDamageImage(Integer parcelId, MultipartFile image, String description) throws IOException {
    Parcel parcel = getParcelById(parcelId);

    String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
    Path imagePath = Paths.get("uploads/damaged-parcels", fileName);
    Files.createDirectories(imagePath.getParent());
    Files.write(imagePath, image.getBytes());

    String imageUrl = "/uploads/damaged-parcels/" + fileName;
    parcel.setDamageImageUrl(imageUrl);
    parcel.setDamageDescription(description);
    parcel.setDamageReportedAt(LocalDateTime.now());

    parcelRepository.save(parcel);
    return imageUrl;
  }

  // ---------- Stats ----------
  public long getTotalParcels() {
    return parcelRepository.count();
  }

  public double getDamagedParcelsPercentage() {
    long total = parcelRepository.count();
    if (total == 0) return 0.0;
    long damaged = parcelRepository.countDamagedParcels();
    return ((double) damaged / total) * 100.0;
  }
}
