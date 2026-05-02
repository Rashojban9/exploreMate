package com.exploreMate.notification_service.controller;

import com.exploreMate.notification_service.dto.NotificationDto;
import com.exploreMate.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@RequestHeader("X-User-Email") String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(notificationService.getUserNotifications(userEmail));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestHeader("X-User-Email") String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        long count = notificationService.getUnreadCount(userEmail);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, @RequestHeader("X-User-Email") String userEmail) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestHeader("X-User-Email") String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllAsRead(userEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications(@RequestHeader("X-User-Email") String userEmail) {
        notificationService.deleteAllUserNotifications(userEmail);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestBody Map<String, String> body) {
        String type = body.getOrDefault("type", "info");
        String title = body.getOrDefault("title", "");
        String message = body.getOrDefault("message", "You have a new notification");
        return ResponseEntity.ok(notificationService.createNotification(userEmail, type, title, message));
    }

    // For manual testing/admin (legacy endpoint kept for backwards compatibility)
    @PostMapping("/test")
    public ResponseEntity<NotificationDto> createTestNotification(
            @RequestParam String email,
            @RequestParam String type,
            @RequestParam String message) {
        return ResponseEntity.ok(notificationService.createNotification(email, type, message));
    }
}
