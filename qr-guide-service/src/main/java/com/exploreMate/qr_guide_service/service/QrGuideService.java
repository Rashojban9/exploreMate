package com.exploreMate.qr_guide_service.service;

import com.exploreMate.qr_guide_service.dto.QrArtifactRequest;
import com.exploreMate.qr_guide_service.dto.QrArtifactResponse;
import com.exploreMate.qr_guide_service.dto.ScanHistoryResponse;
import com.exploreMate.qr_guide_service.mapper.QrArtifactMapper;
import com.exploreMate.qr_guide_service.mapper.ScanHistoryMapper;
import com.exploreMate.qr_guide_service.model.QrArtifact;
import com.exploreMate.qr_guide_service.model.ScanHistory;
import com.exploreMate.qr_guide_service.repo.QrArtifactRepository;
import com.exploreMate.qr_guide_service.repo.ScanHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QrGuideService {

    private final QrArtifactRepository artifactRepository;
    private final ScanHistoryRepository historyRepository;
    private final QrArtifactMapper artifactMapper;
    private final ScanHistoryMapper historyMapper;

    public QrArtifactResponse getArtifact(String id) {
        QrArtifact artifact = artifactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artifact not found with id: " + id));
        return artifactMapper.toResponse(artifact);
    }

    public List<QrArtifactResponse> getAllArtifacts() {
        return artifactRepository.findAll().stream()
                .map(artifactMapper::toResponse)
                .collect(Collectors.toList());
    }

    public QrArtifactResponse createArtifact(QrArtifactRequest request) {
        if (artifactRepository.existsById(request.getId())) {
            throw new RuntimeException("Artifact with this ID already exists: " + request.getId());
        }
        QrArtifact artifact = artifactMapper.toEntity(request);
        artifact = artifactRepository.save(artifact);
        return artifactMapper.toResponse(artifact);
    }

    public ScanHistoryResponse recordScan(String artifactId, String userEmail) {
        QrArtifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new RuntimeException("Artifact not found with id: " + artifactId));

        // Create new history record
        ScanHistory history = ScanHistory.builder()
                .id(UUID.randomUUID())
                .userEmail(userEmail)
                .artifactId(artifactId)
                .scannedAt(Instant.now())
                .build();
        
        history = historyRepository.save(history);
        return historyMapper.toResponse(history, artifact);
    }

    public List<ScanHistoryResponse> getUserHistory(String userEmail) {
        List<ScanHistory> histories = historyRepository.findAllByUserEmailOrderByScannedAtDesc(userEmail);

        return histories.stream()
                .map(history -> {
                    QrArtifact artifact = artifactRepository.findById(history.getArtifactId()).orElse(null);
                    return historyMapper.toResponse(history, artifact);
                })
                // filter out deleted artifacts
                .filter(response -> response != null && response.getArtifact() != null)
                .collect(Collectors.toList());
    }
}
