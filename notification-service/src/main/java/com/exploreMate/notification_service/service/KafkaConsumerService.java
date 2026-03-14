package com.exploreMate.notification_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    /**
     * Consumes Outbox events published to the 'notification-events' topic.
     * Expected JSON structure depends on the producer, but it should contain
     * the target userEmail, the type of notification, and the message.
     */
    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void consumeNotificationEvent(String message) {
        log.info("Received notification event from Kafka: {}", message);
        try {
            JsonNode root = objectMapper.readTree(message);
            
            // Extract outbox payload (or direct payload if no outbox wrapper)
            JsonNode payload = root.has("payload") ? 
                               // If payload is a stringified JSON, parse it again. If object, use as is.
                               (root.get("payload").isTextual() ? objectMapper.readTree(root.get("payload").asText()) : root.get("payload")) 
                               : root;

            String userEmail = payload.has("userEmail") ? payload.get("userEmail").asText() : "system@system.com";
            String type = payload.has("type") ? payload.get("type").asText() : "info";
            String text = payload.has("message") ? payload.get("message").asText() : "You have a new notification";

            // Process and broadcast the notification
            notificationService.createNotification(userEmail, type, text);
            log.info("Successfully processed and broadcasted notification to {}", userEmail);
            
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", message, e);
        }
    }
}
