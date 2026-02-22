package com.exploreMate.email_service.controller;

import com.exploreMate.email_service.dto.EmailDto;
import com.exploreMate.email_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDto emailDto) {
        emailService.sendEmailDirectly(emailDto);
        return ResponseEntity.ok("Email sent successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Email service is running");
    }
}
