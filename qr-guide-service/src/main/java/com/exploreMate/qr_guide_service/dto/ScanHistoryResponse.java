package com.exploreMate.qr_guide_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScanHistoryResponse {
    private String id;
    private String userEmail;
    private QrArtifactResponse artifact;
    private Instant scannedAt;
}
