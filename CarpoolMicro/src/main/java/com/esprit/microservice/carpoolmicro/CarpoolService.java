package com.esprit.microservice.carpoolmicro;

import com.esprit.microservice.carpoolmicro.Carpool;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CarpoolService implements ICarpoolService {
    private final CarpoolRepository carpoolRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Carpool ajouterCarpool(Carpool carpool) {
        return carpoolRepository.save(carpool);
    }

    @Override
    public Carpool affecterUser(Carpool carpool, String offerId) {
        // Sans UserService, on suppose que offerId (UUID) est valide, vérifié par Keycloak
        carpool.setSimpleUserOffer(offerId);
        return carpoolRepository.save(carpool);
    }

    @Override
    public Carpool joinCarpool(Integer carpoolId, String userId, Integer numberOfPlaces) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));

        if (carpool.getJoinedUserIds().contains(userId)) {
            throw new RuntimeException("User has already joined this carpool!");
        }
        if (carpool.getCarpoolCapacity() < numberOfPlaces) {
            throw new RuntimeException("Not enough capacity for " + numberOfPlaces + " places!");
        }
        if (carpool.getSimpleUserOffer().equals(userId)) {
            throw new RuntimeException("You cannot join your own carpool!");
        }
        if (numberOfPlaces <= 0) {
            throw new RuntimeException("Number of places must be positive!");
        }

        carpool.getJoinedUserIds().add(userId);

        Map<String, Integer> placesMap = getJoinedUsersPlaces(carpool);
        placesMap.put(userId, numberOfPlaces);
        setJoinedUsersPlaces(carpool, placesMap);

        carpool.setCarpoolCapacity(carpool.getCarpoolCapacity() - numberOfPlaces);
        updateCarpoolStatus(carpool);
        return carpoolRepository.save(carpool);
    }

    @Override
    public void deleteCarpool(Integer carpoolId, String offerId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found"));

        if (!carpool.getSimpleUserOffer().equals(offerId)) {
            throw new RuntimeException("You are not authorized to delete this carpool");
        }

        carpoolRepository.delete(carpool);
    }

    @Override
    public void leaveCarpool(Integer carpoolId, String userId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found"));

        if (!carpool.getJoinedUserIds().contains(userId)) {
            throw new RuntimeException("User is not in this carpool");
        }

        Map<String, Integer> placesMap = getJoinedUsersPlaces(carpool);
        Integer numberOfPlaces = placesMap.get(userId);
        if (numberOfPlaces == null) {
            throw new RuntimeException("No place allocation found for user");
        }

        carpool.getJoinedUserIds().remove(userId);
        placesMap.remove(userId);
        setJoinedUsersPlaces(carpool, placesMap);

        carpool.setCarpoolCapacity(carpool.getCarpoolCapacity() + numberOfPlaces);
        updateCarpoolStatus(carpool);
        carpoolRepository.save(carpool);
    }

    @Override
    public List<Carpool> getAllCarpools() {
        return carpoolRepository.findAll();
    }

    @Override
    public Carpool getCarpoolById(Integer carpoolId) {
        return carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
    }

    @Override
    public List<Carpool> getFutureCarpools(String userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<Carpool> futureCarpools = carpoolRepository.findFutureCarpools(today, now, userId);
        List<Carpool> recommendedCarpools = getOrderedRecommendedCarpools(userId);
        Set<Integer> recommendedCarpoolIds = recommendedCarpools.stream()
                .map(Carpool::getCarpoolId)
                .collect(Collectors.toSet());
        return futureCarpools.stream()
                .filter(carpool -> !recommendedCarpoolIds.contains(carpool.getCarpoolId()))
                .collect(Collectors.toList());
    }

    @Override
    public String getCarpoolOffererId(Integer carpoolId) {
        String offererId = carpoolRepository.findOffererIdByCarpoolId(carpoolId);
        if (offererId == null) {
            throw new RuntimeException("Carpool not found!");
        }
        return offererId;
    }

    @Override
    public List<Carpool> getCarpoolsByUser(String userId) {
        return carpoolRepository.findBySimpleUserOffer(userId);
    }

    @Override
    public Carpool updateCarpool(Integer carpoolId, String userId, Carpool updatedCarpool) {
        Carpool existingCarpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
        if (!existingCarpool.getSimpleUserOffer().equals(userId)) {
            throw new RuntimeException("You are not the owner of this carpool!");
        }
        if (!existingCarpool.getJoinedUserIds().isEmpty()) {
            throw new RuntimeException("Cannot update carpool because other users have already joined!");
        }

        existingCarpool.setCarpoolDeparture(updatedCarpool.getCarpoolDeparture());
        existingCarpool.setCarpoolDestination(updatedCarpool.getCarpoolDestination());
        existingCarpool.setCarpoolDate(updatedCarpool.getCarpoolDate());
        existingCarpool.setCarpoolTime(updatedCarpool.getCarpoolTime());
        existingCarpool.setCarpoolCapacity(updatedCarpool.getCarpoolCapacity());
        existingCarpool.setCarpoolPrice(updatedCarpool.getCarpoolPrice());
        existingCarpool.setCarpoolCondition(updatedCarpool.getCarpoolCondition());
        return carpoolRepository.save(existingCarpool);
    }

    @Override
    public void updateCarpoolStatus(Carpool carpool) {
        if (carpool.getCarpoolCapacity() == 0) {
            carpool.setCarpoolStatus(CarpoolStatus.unavailable);
        } else {
            carpool.setCarpoolStatus(CarpoolStatus.available);
        }
    }

    @Override
    public List<String> getUsersWhoJoinedCarpool(Integer carpoolId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
        return new ArrayList<>(carpool.getJoinedUserIds());
    }

    @Override
    public List<Carpool> getCarpoolsJoinedByUser(String userId) {
        return carpoolRepository.findAll().stream()
                .filter(carpool -> carpool.getJoinedUserIds().contains(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Carpool> getOrderedRecommendedCarpools(String userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<Object[]> frequentRoutes = carpoolRepository.findFrequentRoutesByUser(userId);
        List<Carpool> allRecommended = new ArrayList<>();

        for (Object[] route : frequentRoutes) {
            String departure = (String) route[0];
            String destination = (String) route[1];
            List<Carpool> matchingCarpools = carpoolRepository.findFutureCarpoolsByRoute(today, now, departure, destination, userId);
            matchingCarpools.forEach(carpool -> {
                allRecommended.add(carpool);
                log.info("Sent recommended carpool notification for userId: {}, carpoolId: {}", userId, carpool.getCarpoolId());
            });
        }
        return allRecommended;
    }

    @Override
    public Carpool rateCarpoolOfferer(Integer carpoolId, String userId, Boolean liked) {
        throw new UnsupportedOperationException("Rating functionality requires user metadata management");
    }

    @Override
    public void calculateOffererAverageRating(String offererId) {
        throw new UnsupportedOperationException("Rating calculation requires user metadata management");
    }

    @Override
    public String getOffererRating(String offererId) {
        throw new UnsupportedOperationException("Rating retrieval requires user metadata management");
    }

    @Override
    public List<Map<String, Boolean>> getCarpoolRatings(Integer carpoolId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new RuntimeException("Carpool not found!"));
        Map<String, Boolean> ratings = getRatings(carpool);
        List<Map<String, Boolean>> result = new ArrayList<>();
        ratings.forEach((userId, liked) -> {
            Map<String, Boolean> rating = new HashMap<>();
            rating.put(userId, liked);
            result.add(rating);
        });
        return result;
    }

    @Override
    public long getTotalCarpools() {
        log.info("Fetching total number of carpools");
        return carpoolRepository.count();
    }

    @Override
    public List<Map<String, Object>> getTopRatedOfferers(int limit) {
        throw new UnsupportedOperationException("Top rated offerers functionality requires user metadata management");
    }

    private Map<String, Integer> getJoinedUsersPlaces(Carpool carpool) {
        try {
            if (carpool.getJoinedUsersPlaces() == null || carpool.getJoinedUsersPlaces().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(carpool.getJoinedUsersPlaces(), new TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            log.error("Failed to parse joinedUsersPlaces", e);
            throw new RuntimeException("Error parsing joined users places");
        }
    }

    private void setJoinedUsersPlaces(Carpool carpool, Map<String, Integer> placesMap) {
        try {
            carpool.setJoinedUsersPlaces(placesMap.isEmpty() ? null : objectMapper.writeValueAsString(placesMap));
        } catch (Exception e) {
            log.error("Failed to serialize joinedUsersPlaces", e);
            throw new RuntimeException("Error serializing joined users places");
        }
    }

    private Map<String, Boolean> getRatings(Carpool carpool) {
        try {
            if (carpool.getRatings() == null || carpool.getRatings().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(carpool.getRatings(), new TypeReference<Map<String, Boolean>>() {});
        } catch (Exception e) {
            log.error("Failed to parse ratings", e);
            throw new RuntimeException("Error parsing ratings");
        }
    }

    private void setRatings(Carpool carpool, Map<String, Boolean> ratings) {
        try {
            carpool.setRatings(ratings.isEmpty() ? null : objectMapper.writeValueAsString(ratings));
        } catch (Exception e) {
            log.error("Failed to serialize ratings", e);
            throw new RuntimeException("Error serializing ratings");
        }
    }
}