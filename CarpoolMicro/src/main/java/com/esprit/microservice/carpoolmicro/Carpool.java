package com.esprit.microservice.carpoolmicro;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carpool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer carpoolId;
    private String carpoolDeparture ;
    @NonNull
    private String carpoolDestination ;
    @NonNull
    private LocalDate carpoolDate ;
    @NonNull
    private LocalTime carpoolTime ;
    @NonNull
    private Integer carpoolCapacity ;
    private String carpoolCondition ;
    private Float carpoolPrice;
    @Enumerated(EnumType.STRING)
    private CarpoolStatus carpoolStatus=CarpoolStatus.available ;
    private String licensePlate ;
    private LocalDateTime creationTime = LocalDateTime.now();

    @JoinColumn(name = "simple_user_user_id")
    private String simpleUserOffer;

    @ElementCollection
    @CollectionTable(name = "carpool_joined_users", joinColumns = @JoinColumn(name = "carpool_id"))
    @Column(name = "user_id")
    private Set<String> joinedUserIds = new LinkedHashSet<>();

    @Column(columnDefinition = "TEXT")
    private String joinedUsersPlaces;

    @Column(columnDefinition = "TEXT")
    private String ratings;

}