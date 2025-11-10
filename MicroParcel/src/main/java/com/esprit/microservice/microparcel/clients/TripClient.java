package com.esprit.microservice.microparcel.clients;

import com.esprit.microservice.microparcel.DTO.TripDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "MICROSERVICETRIP", url = "http://localhost:8088/trip") // port de Trip MS

public interface TripClient {

        @GetMapping("/getTrip/{tripId}")
        TripDTO getTripById(@PathVariable("tripId") Integer tripId);

        @GetMapping("/getAllTrips")
        List<TripDTO> getAllTrips();
    }

