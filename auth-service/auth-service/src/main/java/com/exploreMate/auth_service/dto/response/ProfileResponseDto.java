package com.exploreMate.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {
    private String id;
    private String email;
    private String name;
    private String role;
    private Long numericId;
    private String phoneNumber;
    private String profilePicture;
    private String bio;
    private String title;
    private String location;
    private String dateOfBirth;
    private java.util.List<String> interests;
    private Integer budget;
    private String travelStyle;
    private Instant createdAt;
    private Instant updatedAt;
}
