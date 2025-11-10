package com.esprit.microservice.speedygo.controllers;

import com.esprit.microservice.speedygo.entities.Event;
import com.esprit.microservice.speedygo.services.EventService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    // === CRUD Event ===
    @GetMapping("/getAllEvent")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/getEvent/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        Event created = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/updateEvent/{id}")
    public Event updateEvent(@PathVariable Integer id , @RequestBody Event event){
        event.setEventId(id);
        return eventService.updateEvent(event);
    }

    @DeleteMapping("/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // === Endpoints utilisateurs ===
    // Ces endpoints sont commentés car ils ne font pas partie du microservice Event
    // et doivent être déplacés dans un service User / Registration.

    /*
    @PostMapping("/register/{idEvent}/{userId}")
    public ResponseEntity<?> registerUser(
            @PathVariable Integer idEvent,
            @PathVariable Integer userId
    ) {
        try {
            eventService.registerUser(idEvent, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/unregister/{idEvent}/{userId}")
    public ResponseEntity<?> unregisterUser(
            @PathVariable Integer idEvent,
            @PathVariable Integer userId
    ) {
        try {
            eventService.unregisterUser(idEvent, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/mostParticipants")
    public ResponseEntity<Event> getEventWithMostParticipants() {
        return ResponseEntity.ok(eventService.getEventWithMostParticipants());
    }
    */
}
