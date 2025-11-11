package com.esprit.microservice.carpoolmicro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class CarpoolMicroApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarpoolMicroApplication.class, args);
    }

}
