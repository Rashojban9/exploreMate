package com.exploreMate.qr_guide_service.config;

import com.exploreMate.qr_guide_service.model.QrArtifact;
import com.exploreMate.qr_guide_service.repo.QrArtifactRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initArtifacts(QrArtifactRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                QrArtifact artifact = QrArtifact.builder()
                    .id("artifact-1")
                    .title("Patan Durbar Square")
                    .location("Lalitpur, Nepal")
                    .description("Patan Durbar Square is situated at the centre of the city of Lalitpur in Nepal. It is one of the three Durbar Squares in the Kathmandu Valley, all of which are UNESCO World Heritage Sites. One of its attraction is the ancient royal palace where the Malla Kings of Lalitpur resided.")
                    .image("https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&q=80&w=800")
                    .year("14th Century")
                    .audioDuration("4m 20s")
                    .tags(List.of("Heritage", "Palace", "Architecture"))
                    .build();
                
                repository.save(artifact);
                
                System.out.println("Seeded artifact-1 in QR Guide Service");
            }
        };
    }
}
