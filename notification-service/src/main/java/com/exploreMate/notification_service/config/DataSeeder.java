package com.exploreMate.notification_service.config;

import com.exploreMate.notification_service.model.Notification;
import com.exploreMate.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final NotificationRepository repository;

    @Override
    public void run(String... args) {
        try {
            if (repository.count() == 0) {
                log.info("Seeding sample notifications...");
                
                String sampleEmail = "demo@exploremate.com";
                
                List<Notification> sampleNotifications = List.of(
                    Notification.builder()
                        .userEmail(sampleEmail)
                        .type("warning")
                        .title("Rain Alert")
                        .message("Heavy rain expected in Pokhara tomorrow. Consider rescheduling outdoor treks.")
                        .isRead(false)
                        .createdAt(Instant.now().minus(10, ChronoUnit.MINUTES))
                        .build(),
                    Notification.builder()
                        .userEmail(sampleEmail)
                        .type("success")
                        .title("Trip Confirmed")
                        .message("Your booking for \"Kathmandu Heritage Walk\" has been confirmed.")
                        .isRead(false)
                        .createdAt(Instant.now().minus(2, ChronoUnit.HOURS))
                        .build(),
                    Notification.builder()
                        .userEmail(sampleEmail)
                        .type("info")
                        .title("Upcoming Flight")
                        .message("Flight to Lukla departs in 2 days. Check your packing list.")
                        .isRead(true)
                        .createdAt(Instant.now().minus(5, ChronoUnit.HOURS))
                        .build(),
                    Notification.builder()
                        .userEmail(sampleEmail)
                        .type("info")
                        .title("New Feature")
                        .message("Try our new offline maps for the Annapurna Circuit.")
                        .isRead(true)
                        .createdAt(Instant.now().minus(1, ChronoUnit.DAYS))
                        .build(),
                    Notification.builder()
                        .userEmail(sampleEmail)
                        .type("info")
                        .title("Review Your Trip")
                        .message("How was your visit to Swayambhunath? Leave a review to help others.")
                        .isRead(true)
                        .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                        .build()
                );
                
                repository.saveAll(sampleNotifications);
                log.info("Successfully seeded {} sample notifications", sampleNotifications.size());
            } else {
                log.info("Notifications collection already has data, skipping seed.");
            }
        } catch (Exception e) {
            log.warn("DataSeeder: Could not seed notifications (DB may not be ready yet): {}", e.getMessage());
        }
    }
}
