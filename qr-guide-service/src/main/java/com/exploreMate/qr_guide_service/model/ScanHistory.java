package com.exploreMate.qr_guide_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "scan_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanHistory {
    @Id
    private UUID id;
    
    private String userEmail;
    private String artifactId;
    
    private Instant scannedAt;
}
