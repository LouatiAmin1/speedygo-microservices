package com.esprit.microservice.microparcel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class MicroParcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroParcelApplication.class, args);
    }

}
