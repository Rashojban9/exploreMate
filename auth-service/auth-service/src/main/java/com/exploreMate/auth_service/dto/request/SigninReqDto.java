package com.exploreMate.auth_service.dto.request;

import lombok.Builder;


@Builder

public record SigninReqDto(String email,
                           String password) {

}
