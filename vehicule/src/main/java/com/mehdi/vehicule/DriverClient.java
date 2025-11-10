package com.mehdi.vehicule;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "DRIVER-MS", url = "http://localhost:8081")
public interface DriverClient {

    @PutMapping("/driver/assignVehicle/{driverId}")
    void assignVehicleToDriver(@PathVariable("driverId") Integer driverId,
                               @RequestBody Map<String, Integer> body);
}
