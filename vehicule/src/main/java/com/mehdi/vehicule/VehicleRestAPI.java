package com.mehdi.vehicule;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/vehicle")
public class VehicleRestAPI {

    private VehicleService vehicleService;
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Vehicule addVehicule(@RequestBody Vehicule vehicule) {
        return vehicleService.addVehicule(vehicule); }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Vehicule updateVehicule(@PathVariable int id, @RequestBody Vehicule vehicule) {
        return vehicleService.updateVehicule(vehicule);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public Vehicule getVehiculeById(@PathVariable int id) {
        return vehicleService.getVehiculeById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteVehiculeById(@PathVariable int id) {
        vehicleService.deleteVehiculeById(id);
    }

    @GetMapping("/getAllVehicle")
    @PreAuthorize("hasAnyRole('USER')")
    public List<Vehicule> getAllVehicules() {
        return vehicleService.getAllVehicules();
    }

    @PutMapping("/assign/{vehicleId}/{driverId}")
    public void assignVehicleToDriverEndpoint(@PathVariable Integer vehicleId,
                                              @PathVariable Integer driverId) {
        vehicleService.assignVehicleToDriver(vehicleId, driverId);
    }

}
