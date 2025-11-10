package com.example.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Trip routes
                        .pathMatchers("/eureka/**", "/trip/welcome").permitAll()
                        .pathMatchers("/trip/payments", "/trip/payments/**").authenticated()
                        .pathMatchers("/trip/getAllTrips").authenticated()
                        .pathMatchers("/trip/createTrip", "/trip/getTripsForUser").hasAuthority("ROLE_SIMPLE_USER")
                        .pathMatchers("/trip/acceptTrip/**", "/trip/refuseTrip/**", "/trip/completeTrip/**", "/trip/getTripsForDriver").hasAuthority("ROLE_DRIVER")
                        .pathMatchers("/trip/getTrip/**", "/trip/updateTrip/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_DRIVER")
                        .pathMatchers("/trip/deleteTrip/**").hasAuthority("ROLE_ADMIN")
                        // Parcel routes
                        .pathMatchers("/api/parcels/welcome").permitAll()
                        .pathMatchers("/api/parcels/stats/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers("/api/parcels/{id}/shipped", "/api/parcels/{id}/delivered").hasAuthority("ROLE_DRIVER")
                        .pathMatchers("/api/parcels", "/api/parcels/**").hasAuthority("ROLE_SIMPLE_USER")
                        //CARPOOL routes
                        .pathMatchers("/api/carpool/carpools/get").hasAnyAuthority("ROLE_SIMPLE_USER", "ROLE_ADMIN")
                        .pathMatchers("/api/carpool/carpools/**").hasAuthority("ROLE_SIMPLE_USER")
                        .anyExchange().authenticated()

                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(
                                new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter())
                        ))
                );
        return http.build();
    }
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
            List<String> roles = realmAccess != null
                    ? (List<String>) realmAccess.get("roles")
                    : Collections.emptyList();

            var authorities = roles.stream()
                    .filter(r -> !r.equals("offline_access"))
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                    .toList();

            System.out.println("JWT ROLES DÉTECTÉS : " + roles);
            System.out.println("AUTHORITIES CRÉÉES : " + authorities);

            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

}