package com.exploreMate.auth_service.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserAccount {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String name;
    private String passwordHash;
    private Set<String> roles;
    private String role; // Single role for easier access
    private Long numericId; // Numeric ID for API responses
    private boolean enabled;
    private boolean locked;
    private Instant lastLoginAt;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @Version
    private Long version;
    
    public String getRole() {
        if (role != null) return role;
        if (roles != null && !roles.isEmpty()) {
            return roles.iterator().next();
        }
        return "USER";
    }
    
    public Long getUserId() {
        return numericId != null ? numericId : (id != null ? (long) id.hashCode() : 0L);
    }
}
