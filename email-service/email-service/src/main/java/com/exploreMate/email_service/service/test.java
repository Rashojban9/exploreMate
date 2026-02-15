package com.exploreMate.email_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class test {
    private final EmailService service;
    @PostMapping("/test")
    public void send(@RequestBody dto dto){

        service.sendBookingEmail(dto.getToEmail(), dto.getSubject(), dto.getBody());
    }
}
