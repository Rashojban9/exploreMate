package com.exploreMate.auth_service.dto.request;

public record AdminUserUpdateReqDto(
    String role,
    Boolean enabled,
    Boolean locked
) {}
