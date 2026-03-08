package com.exploreMate.auth_service.dto;

import lombok.Data;

@Data
public class EmailDto {

    private String email;
    private String subject;
    private String body;

}
