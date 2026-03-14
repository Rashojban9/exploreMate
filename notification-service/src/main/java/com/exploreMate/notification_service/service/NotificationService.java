package com.exploreMate.notification_service.service;

import com.exploreMate.notification_service.dto.NotificationDto;
import com.exploreMate.notification_service.model.Notification;
import com.exploreMate.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationDto createNotification(String userEmail, String type, String message) {
        Notification notification = Notification.builder()
                .userEmail(userEmail)
                .type(type)
                .message(message)
                .build();
                
        Notification saved = repository.save(notification);
        NotificationDto dto = mapToDto(saved);
        
        // Push to WebSocket
        log.info("Pushing notification to /queue/notifications for user: {}", userEmail);
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", dto);
        
        return dto;
    }

    public List<NotificationDto> getUserNotifications(String userEmail) {
        return repository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void markAsRead(String id) {
        repository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            repository.save(notification);
        });
    }

    public void deleteNotification(String id) {
        repository.deleteById(id);
    }
    
    public void deleteAllUserNotifications(String userEmail) {
        List<Notification> notifications = repository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        repository.deleteAll(notifications);
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userEmail(notification.getUserEmail())
                .type(notification.getType())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
