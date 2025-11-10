package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("TripService", r -> r.path("/trip/**")
						.uri("lb://MICROSERVICETRIP"))
				.route("ParcelService", r -> r.path("/api/parcels/**")
						.uri("lb://MICROPARCEL"))
				.route("NotificationService", r -> r.path("/notifications/**")
						.uri("lb://MICRONOTIFICATION"))
				.route("EventService", r -> r.path("/event/**")
						.uri("lb://EVENT-SERVICE"))
				.route("CARPOOLMICRO", r -> r.path("/api/carpool/**")
						.uri("lb://CarpoolMicro"))
				.route("FinanceService", r -> r.path("/finance/**")
						.uri("lb://FINANCIAL-SERVICE"))
				.route("VEHICULE-MS", r -> r.path("/vehicle/**")
						.uri("lb://VEHICULE-MS"))
				.build();
	}


}