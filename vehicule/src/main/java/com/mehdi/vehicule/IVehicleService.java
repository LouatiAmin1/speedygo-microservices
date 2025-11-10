package com.mehdi.vehicule;

import java.util.List;

public interface IVehicleService {
    Vehicule addVehicule(Vehicule vehicule);
    Vehicule updateVehicule(Vehicule vehicule);
    Vehicule getVehiculeById(int id);
    void deleteVehiculeById(int id);
    List<Vehicule> getAllVehicules();
}
