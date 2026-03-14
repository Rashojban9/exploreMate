package com.exploreMate.qr_guide_service.mapper;

import com.exploreMate.qr_guide_service.dto.QrArtifactRequest;
import com.exploreMate.qr_guide_service.dto.QrArtifactResponse;
import com.exploreMate.qr_guide_service.model.QrArtifact;
import org.springframework.stereotype.Component;

@Component
public class QrArtifactMapper {

    public QrArtifact toEntity(QrArtifactRequest request) {
        return QrArtifact.builder()
                .id(request.getId())
                .title(request.getTitle())
                .location(request.getLocation())
                .description(request.getDescription())
                .image(request.getImage())
                .audioDuration(request.getAudioDuration())
                .year(request.getYear())
                .tags(request.getTags())
                .build();
    }

    public QrArtifactResponse toResponse(QrArtifact artifact) {
        if (artifact == null) return null;
        
        return QrArtifactResponse.builder()
                .id(artifact.getId())
                .title(artifact.getTitle())
                .location(artifact.getLocation())
                .description(artifact.getDescription())
                .image(artifact.getImage())
                .audioDuration(artifact.getAudioDuration())
                .year(artifact.getYear())
                .tags(artifact.getTags())
                .build();
    }
}
