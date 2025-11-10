package com.esprit.microservice.carpoolmicro;



import com.esprit.microservice.carpoolmicro.Carpool;
import com.esprit.microservice.carpoolmicro.CarpoolStatus;

import java.util.List;
import java.util.Map;

public interface ICarpoolService {
    Carpool ajouterCarpool(Carpool carpool);
    Carpool affecterUser(Carpool carpool, String offerId); // Changé en String
    Carpool joinCarpool(Integer carpoolId, String userId, Integer numberOfPlaces); // Changé en String
    void leaveCarpool(Integer carpoolId, String userId); // Changé en String
    void deleteCarpool(Integer carpoolId, String offerId); // Changé en String
    List<Carpool> getAllCarpools();
    Carpool getCarpoolById(Integer carpoolId);
    String getCarpoolOffererId(Integer carpoolId); // Retourne String au lieu de Integer
    List<Carpool> getCarpoolsByUser(String userId); // Changé en String
    Carpool updateCarpool(Integer carpoolId, String userId, Carpool updatedCarpool); // Changé en String
    void updateCarpoolStatus(Carpool carpool);
    List<String> getUsersWhoJoinedCarpool(Integer carpoolId); // Retourne List<String> au lieu de List<Integer>
    List<Carpool> getCarpoolsJoinedByUser(String userId); // Changé en String
    List<Carpool> getOrderedRecommendedCarpools(String userId); // Changé en String
    List<Carpool> getFutureCarpools(String userId); // Changé en String
    Carpool rateCarpoolOfferer(Integer carpoolId, String userId, Boolean liked); // Changé en String
    void calculateOffererAverageRating(String offererId); // Changé en String
    String getOffererRating(String offererId); // Changé en String
    List<Map<String, Boolean>> getCarpoolRatings(Integer carpoolId); // Changé en Map<String, Boolean>
    long getTotalCarpools();
    List<Map<String, Object>> getTopRatedOfferers(int limit);
}