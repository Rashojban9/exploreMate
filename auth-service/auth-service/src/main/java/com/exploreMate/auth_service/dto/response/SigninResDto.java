package com.exploreMate.auth_service.dto.response;

import lombok.Builder;

@Builder
public record SigninResDto(
    String token,
    Long userId,
    String name,
    String email,
    String role
) {

}
