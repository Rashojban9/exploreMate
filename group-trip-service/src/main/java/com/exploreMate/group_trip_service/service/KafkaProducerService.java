package com.exploreMate.group_trip_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "notification-events";

    public void sendNotification(String userEmail, String type, String message) {
        try {
            Map<String, Object> payload = Map.of(
                "userEmail", userEmail,
                "type", type,
                "message", message
            );
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(TOPIC, json);
            log.info("Sent notification event to Kafka for {}: {}", userEmail, message);
        } catch (Exception e) {
            log.error("Failed to send notification event to Kafka", e);
        }
    }
}
