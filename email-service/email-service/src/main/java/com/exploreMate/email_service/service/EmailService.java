package com.exploreMate.email_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.exploreMate.email_service.dto.EmailDto;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Service
@Slf4j
public class EmailService {

    private final Resend resend;

    @Value("${resend.api.key}")
    private String resendApiKey;

    public EmailService() {
        // Will be initialized with the API key from @Value
        this.resend = null;
    }

    private Resend getResend() {
        if (resend == null && resendApiKey != null) {
            return new Resend(resendApiKey);
        }
        return new Resend("re_f9sg9DmY_KhisbLgzDcphYZuryRsauMX3");
    }

    @KafkaListener(topics = "signup", groupId = "signup-1")
    public void sendWelcomeEmail(Map<String, String> emailMap) {
        log.info("Received signup email request for: {}", emailMap.get("email"));
        sendEmailDirectly(emailMap);
    }

    @KafkaListener(topics = "password-reset", groupId = "password-reset-1")
    public void sendPasswordResetEmail(Map<String, String> emailMap) {
        log.info("Received password reset email request for: {}", emailMap.get("email"));
        sendEmailDirectly(emailMap);
    }

    public void sendEmailDirectly(Map<String, String> emailMap) {
        try {
            String email = emailMap.get("email");
            String subject = emailMap.get("subject");
            String body = emailMap.get("body");
            
            log.info("Preparing to send email to: {}, subject: {}", email, subject);
            Resend resendClient = getResend();
            log.debug("Resend client initialized with API key starting with: {}", 
                resendClient != null ? resendApiKey != null ? resendApiKey.substring(0, Math.min(5, resendApiKey.length())) : "null" : "null");
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("ExploreMate <onboarding@resend.dev>")
                    .to(email)
                    .subject(subject)
                    .html(body)
                    .build();

            CreateEmailResponse data = resendClient.emails().send(params);
            log.info("Email sent successfully! ID: {}", data.getId());
        } catch (ResendException e) {
            log.error("Resend API error - Failed to send email to {}: {}", emailMap.get("email"), e.getMessage(), e);
            // Don't throw - just log the error so message isn't re-processed
        } catch (Exception e) {
            log.error("Unexpected error - Failed to send email to {}: {}", emailMap.get("email"), e.getMessage(), e);
            // Don't throw - just log the error so message isn't re-processed
        }
    }
}
