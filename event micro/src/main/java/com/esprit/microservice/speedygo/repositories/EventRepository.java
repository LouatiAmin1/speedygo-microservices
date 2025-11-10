package com.esprit.microservice.speedygo.repositories;

import com.esprit.microservice.speedygo.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Integer> {
}
