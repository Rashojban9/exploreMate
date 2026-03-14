package com.exploreMate.qr_guide_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "qr_artifacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrArtifact {
    @Id
    private String id; // This will also be the code encoded in the QR
    private String title;
    private String location;
    private String description;
    private String image;
    private String audioDuration;
    private String year;
    private List<String> tags;
}
