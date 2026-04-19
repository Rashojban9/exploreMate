package com.exploreMate.content_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaItem {
    @Id
    private String id;

    private String name;        // e.g. "logo.png"
    private String url;         // direct image URL
    private String type;        // "Image", "Video", "Document"
    private String sizeLabel;   // "512 KB"
    private String status;      // "Live" or "Archived"

    private Instant createdAt;
    private Instant updatedAt;
}
