package com.exploreMate.trip_service.config;

import com.exploreMate.trip_service.model.SavedItem;
import com.exploreMate.trip_service.model.Trip;
import com.exploreMate.trip_service.repo.SavedItemRepository;
import com.exploreMate.trip_service.repo.TripRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initTrips(TripRepository tripRepo, SavedItemRepository savedRepo) {
        return args -> {
            String demoEmail = "admin@exploremate.app";

            if (tripRepo.count() == 0) {
                Trip trip = Trip.builder()
                    .id(UUID.randomUUID())
                    .tripName("Himalayan Adventure")
                    .tripDescription("A journey through the heart of the Himalayas.")
                    .placeName("Mount Everest")
                    .placeDescription("The highest mountain in the world.")
                    .placePhotos(List.of("https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&q=80&w=800"))
                    .userEmail(demoEmail)
                    .startDate("2026-05-10")
                    .endDate("2026-05-25")
                    .status("UPCOMING")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
                
                tripRepo.save(trip);
                System.out.println("Seeded initial trip for " + demoEmail);
            }

            if (savedRepo.count() == 0) {
                List<SavedItem> items = List.of(
                    SavedItem.builder()
                        .id("saved-1")
                        .userEmail(demoEmail)
                        .type("DESTINATION")
                        .title("Pokhara Lake")
                        .location("Pokhara, Nepal")
                        // matches Dashboard mock
                        .imageUrl("https://images.unsplash.com/photo-1552422554-0d5af0c79fc6?auto=format&fit=crop&q=80&w=400")
                        .description("Serene lake with mountain views.")
                        .dateAdded(LocalDateTime.now())
                        .build(),
                    SavedItem.builder()
                        .id("saved-2")
                        .userEmail(demoEmail)
                        .type("ADVENTURE")
                        .title("Everest Trek")
                        .location("Solu Khumbu")
                        .imageUrl("https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&q=80&w=400")
                        .description("The legendary trek to EBC.")
                        .dateAdded(LocalDateTime.now())
                        .build(),
                    SavedItem.builder()
                        .id("saved-3")
                        .userEmail(demoEmail)
                        .type("HERITAGE")
                        .title("Boudha Stupa")
                        .location("Kathmandu")
                        .imageUrl("https://images.unsplash.com/photo-1574484284008-81dcec28d3e7?auto=format&fit=crop&q=80&w=400")
                        .description("Largest stupa in Nepal.")
                        .dateAdded(LocalDateTime.now())
                        .build()
                );
                
                savedRepo.saveAll(items);
                System.out.println("Seeded initial saved items for " + demoEmail);
            }
        };
    }
}
