package com.esprit.microservice.speedygo.services;

import com.esprit.microservice.speedygo.entities.Event;
import com.esprit.microservice.speedygo.repositories.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class EventService implements IEventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Integer idEvent) {
        return eventRepository.findById(idEvent)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + idEvent));
    }

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        if (!eventRepository.existsById(event.getEventId())) {
            throw new RuntimeException("Event not found with id: " + event.getEventId());
        }
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Integer idEvent) {
        if (!eventRepository.existsById(idEvent)) {
            throw new RuntimeException("Event not found with id: " + idEvent);
        }
        eventRepository.deleteById(idEvent);
    }

    // Méthodes liées aux utilisateurs/commentées pour microservice Event
    // @Override
    // public void registerUser(Integer idEvent, Integer userId) { ... }

    // @Override
    // public void unregisterUser(Integer idEvent, Integer userId) { ... }

    // @Override
    // public List<Event> getAllEventsForUser(Integer userId) { ... }

    // @Override
    // public Event getEventWithMostParticipants() { ... }
}
