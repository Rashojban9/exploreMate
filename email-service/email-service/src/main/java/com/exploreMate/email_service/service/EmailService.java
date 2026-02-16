package com.exploreMate.email_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    public void sendBookingEmail(String toEmail,String subject,String body){
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("exploremate13@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);


            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {

            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
}
