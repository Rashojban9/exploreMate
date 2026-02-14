package com.exploreMate.auth_service.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T>{
    private int status;
    private String message;
    private T data;
    private LocalDate timestamp;
    public static <T>ResponseEntity<ApiResponse<T>> success(String message,T data){
        ApiResponse<T> body=new ApiResponse<T>(200,message,data,LocalDate.now());
        return ResponseEntity.status(200).body(body);
    }
}
