package com.exploreMate.auth_service.utils;

import org.springframework.http.ResponseEntity;

public class Response {
    public static <T> ResponseEntity<ApiResponse<T>> sucess(String message, T data) {
        return ApiResponse.success(message, data);
    }
}
