package com.exploreMate.auth_service.dto.request;

public record ResetPasswordReqDto(String token, String newPassword) {
}
