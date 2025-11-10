package com.esprit.microservice.microparcel.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parcel")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parcelId;

    @Enumerated(EnumType.STRING)
    private ParcelCategory parcelCategory;

    @NotNull
    private String recepeientPhoneNumber;

    @NotNull
    private String senderPhoneNumber;

    @NotNull
    private String parcelDeparture;

    @NotNull
    private String parcelDestination;

    private Double parcelWeight;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Date parcelDate;

    private Float parcelPrice;

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean archived = false;

    // Damaged parcel
    private String damageImageUrl;
    private String damageDescription;
    private LocalDateTime damageReportedAt;

    // Supprimé Keycloak pour rendre le service indépendant
}
