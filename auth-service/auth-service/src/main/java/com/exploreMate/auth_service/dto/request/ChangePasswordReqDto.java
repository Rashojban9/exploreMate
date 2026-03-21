package com.exploreMate.auth_service.dto.request;

public record ChangePasswordReqDto(
    String currentPassword,
    String newPassword
) {}
