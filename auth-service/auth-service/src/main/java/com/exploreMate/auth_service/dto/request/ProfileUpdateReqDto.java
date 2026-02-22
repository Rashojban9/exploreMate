package com.exploreMate.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateReqDto {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String profilePicture;

    private String bio;

    private String title;
    private String location;
    private String dateOfBirth;
    private java.util.List<String> interests;
    private Integer budget;
    private String travelStyle;
}
