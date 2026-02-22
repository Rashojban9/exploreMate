package com.exploreMate.email_service.service;

import com.exploreMate.email_service.dto.EmailDto;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final Resend resend;

    @Value("${resend.api.key:re_f9sg9DmY_KhisbLgzDcphYZuryRsauMX3}")
    private String resendApiKey;

    public EmailService() {
        this.resend = new Resend("re_f9sg9DmY_KhisbLgzDcphYZuryRsauMX3");
    }

    @KafkaListener(topics = "signup", groupId = "signup-1")
    public void sendWelcomeEmail(EmailDto email) {
        sendEmailDirectly(email);
    }

    public void sendEmailDirectly(EmailDto emailDto) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("ExploreMate <onboarding@resend.dev>")
                    .to(emailDto.getEmail())
                    .subject(emailDto.getSubject())
                    .html(emailDto.getBody())
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            log.info("Email sent successfully! ID: {}", data.getId());
        } catch (ResendException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
