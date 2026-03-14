package com.exploreMate.notification_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    
    @Indexed
    private String userEmail; 
    
    // Type of notification: 'info', 'success', 'warning', 'error'
    private String type;
    
    private String message;
    
    @Builder.Default
    private boolean isRead = false;
    
    @Builder.Default
    private Instant createdAt = Instant.now();
}
