package com.esprit.microservice.microparcel.repositories;

import com.esprit.microservice.microparcel.entities.Parcel;
import com.esprit.microservice.microparcel.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Integer> {

    List<Parcel> findByStatus(Status status);

    List<Parcel> findByDamageImageUrlIsNotNull();

    List<Parcel> findByStatusAndArchivedFalse(Status status);

    @Query("SELECT p FROM Parcel p WHERE p.parcelDate > :date")
    List<Parcel> findParcelsAfterDate(Date date);

    @Query("SELECT p FROM Parcel p WHERE p.parcelDate < :date")
    List<Parcel> findParcelsBeforeDate(Date date);

    @Query("SELECT COUNT(p) FROM Parcel p WHERE p.status = 'DELIVERED' AND p.parcelDate >= :start AND p.parcelDate < :end")
    long countDeliveredParcelsBetween(Date start, Date end);

    @Query("SELECT COUNT(p) FROM Parcel p")
    long countAllParcels();

    @Query("SELECT COUNT(p) FROM Parcel p WHERE p.damageImageUrl IS NOT NULL")
    long countDamagedParcels();

    // Recherche simple par departure/destination
    @Query("SELECT p FROM Parcel p WHERE (:departure IS NULL OR LOWER(p.parcelDeparture) LIKE LOWER(CONCAT('%', :departure, '%'))) AND (:destination IS NULL OR LOWER(p.parcelDestination) LIKE LOWER(CONCAT('%', :destination, '%')))")
    List<Parcel> searchParcels(String departure, String destination);
}
