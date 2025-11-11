package com.esprit.microservice.carpoolmicro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CarpoolRepository extends JpaRepository<Carpool, Integer> {

    @Query("SELECT c FROM Carpool c " +
            "WHERE (c.carpoolDate > :currentDate OR (c.carpoolDate = :currentDate AND c.carpoolTime > :currentTime)) " +
            "AND c.carpoolCapacity > 0 " +
            "AND c.simpleUserOffer <> :userId " +
            "AND :userId NOT IN (SELECT u FROM c.joinedUserIds u)")
    List<Carpool> findFutureCarpools(@Param("currentDate") LocalDate currentDate,
                                     @Param("currentTime") LocalTime currentTime,
                                     @Param("userId") String userId); // Changé en String

    @Query("SELECT c FROM Carpool c " +
            "WHERE (c.carpoolDate > :today OR (c.carpoolDate = :today AND c.carpoolTime > :now)) " +
            "AND c.carpoolDeparture = :departure AND c.carpoolDestination = :destination " +
            "AND c.carpoolCapacity > 0 " +
            "AND c.simpleUserOffer <> :userId " +
            "AND :userId NOT IN (SELECT u FROM c.joinedUserIds u)")
    List<Carpool> findFutureCarpoolsByRoute(@Param("today") LocalDate today,
                                            @Param("now") LocalTime now,
                                            @Param("departure") String departure,
                                            @Param("destination") String destination,
                                            @Param("userId") String userId); // Changé en String

    @Query("SELECT c.simpleUserOffer FROM Carpool c WHERE c.carpoolId = :carpoolId")
    String findOffererIdByCarpoolId(@Param("carpoolId") Integer carpoolId); // Changé en String

    List<Carpool> findBySimpleUserOffer(@Param("userId") String userId); // Changé en String

    @Query("SELECT c.carpoolDeparture, c.carpoolDestination, COUNT(c) as freq " +
            "FROM Carpool c " +
            "WHERE :userId IN (SELECT u FROM c.joinedUserIds u) " +
            "GROUP BY c.carpoolDeparture, c.carpoolDestination " +
            "ORDER BY freq DESC")
    List<Object[]> findFrequentRoutesByUser(@Param("userId") String userId); // Changé en String
}