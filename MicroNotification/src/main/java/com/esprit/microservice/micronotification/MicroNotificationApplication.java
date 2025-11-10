package com.esprit.microservice.micronotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class MicroNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroNotificationApplication.class, args);
    }

}
