package com.esprit.microservice.carpoolmicro;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carpools")
@AllArgsConstructor
public class CarpoolController {

    private final CarpoolService carpoolService;

    @Value("${welcome.message}")
    private String welcomeMessage;

    // Constructeur pour l'injection de dépendance
    @Autowired
    public CarpoolController(CarpoolService carpoolService) {
        this.carpoolService = carpoolService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }

    // Endpoint pour ajouter un covoiturage
    @PostMapping("/add")
    public Carpool ajouterCarpool(@RequestBody Carpool carpool) {
        return carpoolService.ajouterCarpool(carpool);
    }

    // Endpoint pour associer un utilisateur à un covoiturage existant
    @PostMapping("/affecter/{offerId}/{carpoolId}")
    public Carpool affecterUser(@PathVariable String offerId, @PathVariable Integer carpoolId) {
        Carpool carpool = carpoolService.getCarpoolById(carpoolId);
        return carpoolService.affecterUser(carpool, offerId);
    }

    // Endpoint pour ajouter un covoiturage et l'associer immédiatement
    @PostMapping("/add/{offerId}")
    public Carpool addCarpool(@RequestBody Carpool carpool, @PathVariable String offerId) {
        Carpool savedCarpool = carpoolService.ajouterCarpool(carpool);
        return carpoolService.affecterUser(savedCarpool, offerId);
    }

    // Endpoint pour rejoindre un covoiturage
    @PostMapping("/join/{carpoolId}/{simpleUserId}")
    public Carpool joinCarpool(@PathVariable Integer carpoolId, @PathVariable String simpleUserId, @RequestBody Integer numberOfPlaces) {
        return carpoolService.joinCarpool(carpoolId, simpleUserId, numberOfPlaces);
    }

    // Endpoint pour quitter un covoiturage
    @DeleteMapping("/leave/{carpoolId}/{userId}")
    public void leaveCarpool(@PathVariable Integer carpoolId, @PathVariable String userId) {
        carpoolService.leaveCarpool(carpoolId, userId);
    }

    // Endpoint pour supprimer un covoiturage
    @DeleteMapping("/delete/{carpoolId}/{offerId}")
    public void deleteCarpool(@PathVariable Integer carpoolId, @PathVariable String offerId) {
        carpoolService.deleteCarpool(carpoolId, offerId);
    }

    // Endpoint pour récupérer tous les covoiturages
    @GetMapping("/get")
    public List<Carpool> getAllCarpools() {
        return carpoolService.getAllCarpools();
    }

    // Endpoint pour récupérer les covoiturages futurs
    @GetMapping("/future")
    public List<Carpool> getFutureCarpools(@RequestParam String userId) {
        return carpoolService.getFutureCarpools(userId);
    }

    // Endpoint pour récupérer un covoiturage par ID
    @GetMapping("/get/{carpoolId}")
    public Carpool getCarpoolById(@PathVariable Integer carpoolId) {
        return carpoolService.getCarpoolById(carpoolId);
    }

    // Endpoint pour récupérer l'ID de l'offreur d'un covoiturage
    @GetMapping("/{carpoolId}/offreur")
    public ResponseEntity<String> getCarpoolOfferer(@PathVariable Integer carpoolId) {
        String offererId = carpoolService.getCarpoolOffererId(carpoolId);
        if (offererId != null) {
            return ResponseEntity.ok(offererId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint pour récupérer les covoiturages offerts par un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Carpool>> getCarpoolsByUser(@PathVariable String userId) {
        List<Carpool> carpools = carpoolService.getCarpoolsByUser(userId);
        return ResponseEntity.ok(carpools);
    }

    // Endpoint pour mettre à jour un covoiturage
    @PutMapping("/update/{carpoolId}/{userId}")
    public ResponseEntity<?> updateCarpool(@PathVariable Integer carpoolId, @PathVariable String userId, @RequestBody Carpool updatedCarpool) {
        try {
            Carpool carpool = carpoolService.updateCarpool(carpoolId, userId, updatedCarpool);
            return ResponseEntity.ok(carpool);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint pour récupérer les IDs des utilisateurs ayant rejoint un covoiturage
    @GetMapping("/{carpoolId}/users")
    public List<String> getUsersWhoJoinedCarpool(@PathVariable Integer carpoolId) {
        return carpoolService.getUsersWhoJoinedCarpool(carpoolId);
    }

    // Endpoint pour récupérer les covoiturages auxquels un utilisateur a rejoint
    @GetMapping("/joined/{userId}")
    public ResponseEntity<List<Carpool>> getCarpoolsJoinedByUser(@PathVariable String userId) {
        List<Carpool> carpools = carpoolService.getCarpoolsJoinedByUser(userId);
        return ResponseEntity.ok(carpools);
    }

    // Endpoint pour récupérer les covoiturages recommandés
    @GetMapping("/recommended/{userId}")
    public List<Carpool> getRecommended(@PathVariable String userId) {
        return carpoolService.getOrderedRecommendedCarpools(userId);
    }

    // Endpoint pour noter un covoiturage
    @PostMapping("/rate")
    public ResponseEntity<?> rateCarpool(@RequestBody RateCarpoolRequest request) {
        try {
            Carpool carpool = carpoolService.rateCarpoolOfferer(
                    request.getCarpoolId(),
                    request.getUserId(),
                    request.getLiked()
            );
            return ResponseEntity.ok(carpool);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body("Rating functionality is not supported without user metadata management");
        }
    }

    // Endpoint pour récupérer les ratings d'un covoiturage
    @GetMapping("/{carpoolId}/ratings")
    public List<Map<String, Boolean>> getCarpoolRatings(@PathVariable Integer carpoolId) {
        return carpoolService.getCarpoolRatings(carpoolId);
    }

    // Endpoint pour récupérer la note moyenne d'un offreur
    @GetMapping("/offerer/{offererId}/rating")
    public ResponseEntity<String> getOffererRating(@PathVariable String offererId) {
        try {
            String rating = carpoolService.getOffererRating(offererId);
            return ResponseEntity.ok(rating);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body("Rating retrieval is not supported without user metadata management");
        }
    }


    // Endpoint pour compter le nombre total de covoiturages
    @GetMapping("/count")
    public long getTotalCarpools() {
        return carpoolService.getTotalCarpools();
    }

    // Classe interne pour la requête de notation
    static class RateCarpoolRequest {
        private Integer carpoolId;
        private String userId; // Changé en String
        private Boolean liked;

        public Integer getCarpoolId() {
            return carpoolId;
        }

        public void setCarpoolId(Integer carpoolId) {
            this.carpoolId = carpoolId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Boolean getLiked() {
            return liked;
        }

        public void setLiked(Boolean liked) {
            this.liked = liked;
        }
    }
}