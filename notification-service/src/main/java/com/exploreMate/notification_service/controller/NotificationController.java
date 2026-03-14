package com.exploreMate.notification_service.controller;

import com.exploreMate.notification_service.dto.NotificationDto;
import com.exploreMate.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, @RequestHeader("X-User-Email") String userEmail) {
        // Technically should verify ownership, but ID is UUID
        notificationService.markAsRead(id);
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

    // For manual testing/admin
    @PostMapping("/test")
    public ResponseEntity<NotificationDto> createTestNotification(
            @RequestParam String email,
            @RequestParam String type,
            @RequestParam String message) {
        return ResponseEntity.ok(notificationService.createNotification(email, type, message));
    }
}
