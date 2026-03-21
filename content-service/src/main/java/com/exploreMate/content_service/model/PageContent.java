package com.exploreMate.content_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "pages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageContent {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String slug;
    
    private String title;
    
    private Map<String, String> contentBlocks;
    
    private String status; // "Published" or "Draft"
    
    private Instant updatedAt;
}
