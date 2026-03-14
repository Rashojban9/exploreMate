package com.exploreMate.qr_guide_service.mapper;

import com.exploreMate.qr_guide_service.dto.QrArtifactResponse;
import com.exploreMate.qr_guide_service.dto.ScanHistoryResponse;
import com.exploreMate.qr_guide_service.model.QrArtifact;
import com.exploreMate.qr_guide_service.model.ScanHistory;
import org.springframework.stereotype.Component;

@Component
public class ScanHistoryMapper {

    private final QrArtifactMapper artifactMapper;

    public ScanHistoryMapper(QrArtifactMapper artifactMapper) {
        this.artifactMapper = artifactMapper;
    }

    public ScanHistoryResponse toResponse(ScanHistory history, QrArtifact artifact) {
        if (history == null) return null;

        QrArtifactResponse artifactResponse = artifactMapper.toResponse(artifact);

        return ScanHistoryResponse.builder()
                .id(history.getId().toString())
                .userEmail(history.getUserEmail())
                .artifact(artifactResponse)
                .scannedAt(history.getScannedAt())
                .build();
    }
}
