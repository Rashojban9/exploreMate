package com.exploreMate.email_service.controller;

import com.exploreMate.email_service.dto.EmailDto;
import com.exploreMate.email_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class EmailController {
    @RestController
    @RequiredArgsConstructor
    public static class test {
        private final EmailService service;
        @PostMapping("/test")

        public void send(@RequestBody EmailDto EmailDto){

            service.sendWelcomeEmail(EmailDto.getToEmail(), EmailDto.getSubject(), EmailDto.getBody());
        }
    }
}
