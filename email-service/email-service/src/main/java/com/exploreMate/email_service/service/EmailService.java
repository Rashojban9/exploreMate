package com.exploreMate.email_service.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions; // Use this
import com.resend.services.emails.model.CreateEmailResponse; // Use this
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final Resend resend = new Resend("re_f9sg9DmY_KhisbLgzDcphYZuryRsauMX3");

    public void sendWelcomeEmail(String toEmail, String subject, String htmlContent) {
        try {

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("onboarding@resend.dev")
                    .to(toEmail)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            log.info("Email sent successfully! ID: {}", data.getId());

        } catch (ResendException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}