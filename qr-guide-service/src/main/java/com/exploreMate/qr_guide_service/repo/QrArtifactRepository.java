package com.exploreMate.qr_guide_service.repo;

import com.exploreMate.qr_guide_service.model.QrArtifact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrArtifactRepository extends MongoRepository<QrArtifact, String> {
}
