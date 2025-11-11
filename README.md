# SpeedyGo — Microservices 

SpeedyGo est une plateforme microservices pour la mobilité : covoiturage, colis, trajets, notifications, gestion véhicules et paiements.  
Ce repo contient plusieurs services Spring Boot prêts à être buildés et déployés (voir arborescence en tête).

## Contenu du dépôt

| Nom du dossier       | Description courte |
|--------------------|-----------------|
| `.idea`             | Configuration IDE (IntelliJ) |
| `CarpoolMicro`      | Service de covoiturage |
| `ConfigServer`      | Serveur de configuration Spring Cloud |
| `Eurekaserver`      | Service de découverte (Eureka) |
| `Gateway`           | API Gateway pour centraliser les appels |
| `MicroNotification` | Service de notifications |
| `MicroParcel`       | Service gestion de colis |
| `MicroserviceTrip`  | Service de gestion des voyages |
| `event micro`       | Service de gestion des événements |
| `financial`         | Service de paiement / finances |
| `vehicule`          | Gestion des véhicules |


## Architecture

SpeedyGo utilise une architecture **microservices** :

- **ConfigServer** : centralise les configurations pour tous les microservices.
- **Eurekaserver** : enregistre tous les microservices pour découverte dynamique.
- **Gateway** : point d’entrée unique pour les clients (Angular/React, applications mobiles, etc.).
- Chaque microservice est autonome et communique via HTTP REST.


## Prérequis pour exécuter localement

- Java 17
- Maven 3.6+
- Docker & Docker Compose (optionnel)
- MySQL ou H2 si tu souhaites une DB locale

