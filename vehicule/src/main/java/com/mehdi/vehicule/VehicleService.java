package com.mehdi.vehicule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class VehicleService implements IVehicleService{

    private VehicleRepository vehicleRepository;
    private DriverClient driverClient;

    @Override
    public Vehicule addVehicule(Vehicule vehicule) {
        return vehicleRepository.save(vehicule);
    }

    @Override
    public Vehicule updateVehicule(Vehicule vehicule) {
        return vehicleRepository.save(vehicule);
    }

    @Override
    public Vehicule getVehiculeById(int id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteVehiculeById(int id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public List<Vehicule> getAllVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        vehicleRepository.findAll().forEach(vehicules::add);
        return vehicules;
    }

    public void assignVehicleToDriver(Integer vehicleId, Integer driverId) {
        // 1) vérifie que le véhicule existe (sinon lève exception)
        Vehicule v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable"));

        // 2) appelle driver-service via Feign pour demander l'assign
        driverClient.assignVehicleToDriver(driverId, Map.of("vehicleId", vehicleId));

        vehicleRepository.save(v);
    }
}
