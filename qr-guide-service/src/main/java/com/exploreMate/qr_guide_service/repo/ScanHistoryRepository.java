package com.exploreMate.qr_guide_service.repo;

import com.exploreMate.qr_guide_service.model.ScanHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScanHistoryRepository extends MongoRepository<ScanHistory, UUID> {
    List<ScanHistory> findAllByUserEmailOrderByScannedAtDesc(String userEmail);
    boolean existsByUserEmailAndArtifactId(String userEmail, String artifactId);
}
