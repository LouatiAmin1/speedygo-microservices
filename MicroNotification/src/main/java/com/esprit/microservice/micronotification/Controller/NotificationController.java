package com.esprit.microservice.micronotification.Controller;

import com.esprit.microservice.micronotification.Entity.Notification;
import com.esprit.microservice.micronotification.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Value("${welcome.message}")
    private String welcomeMessage;
    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam String userKeycloakId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userKeycloakId, emitter);

        emitter.onCompletion(() -> emitters.remove(userKeycloakId));
        emitter.onTimeout(() -> emitters.remove(userKeycloakId));

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected to SSE stream for userKeycloakId=" + userKeycloakId));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendNotificationToUser(String message, String userKeycloakId) {
        SseEmitter emitter = emitters.get(userKeycloakId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("NOTIFICATION").data(message));
            } catch (Exception e) {
                emitter.complete();
                emitters.remove(userKeycloakId);
            }
        }
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications(@RequestParam String userKeycloakId) {
        return notificationService.getAllNotificationsForUser(userKeycloakId);
    }
}
