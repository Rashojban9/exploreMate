package com.exploreMate.email_service.dto;

import lombok.Data;

@Data
public class EmailDto {

        private String toEmail;
        private String subject;
        private String body;

}
