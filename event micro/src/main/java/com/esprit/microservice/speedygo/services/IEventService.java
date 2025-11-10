package com.esprit.microservice.speedygo.services;

import com.esprit.microservice.speedygo.entities.Event;
import java.util.List;

public interface IEventService {
    List<Event> getAllEvents();

    Event getEventById(Integer idEvent);

    Event createEvent(Event event);

    Event updateEvent(Event event);

    void deleteEvent(Integer idEvent);

    // Méthodes liées aux utilisateurs/commentées pour microservice Event
    // void registerUser(Integer idEvent, Integer userId);
    // void unregisterUser(Integer idEvent, Integer userId);
    // List<Event> getAllEventsForUser(Integer userId);
    // Event getEventWithMostParticipants();
}
