package com.mehdi.vehicule;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Vehicule {

    @Id
    @GeneratedValue
    private Integer VehicleId;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    private String vehiculeModel;
    private Integer vehicleCapacity;
    private Integer vehicleSerialNumber;
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehiculeMaintenanceDate;
    private Boolean vehiculeInsuranceStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date vehiculeInsuranceDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
}
