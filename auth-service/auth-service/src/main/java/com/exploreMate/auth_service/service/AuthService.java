package com.exploreMate.auth_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.exploreMate.auth_service.dto.request.ProfileUpdateReqDto;
import com.exploreMate.auth_service.dto.request.SigninReqDto;
import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.dto.response.ProfileResponseDto;
import com.exploreMate.auth_service.dto.response.SigninResDto;
import com.exploreMate.auth_service.jwt.jwtUtils.JwtUtils;
import com.exploreMate.auth_service.kafka.KafkaResDto;
import com.exploreMate.auth_service.mapper.AuthMapper;
import com.exploreMate.auth_service.model.PasswordResetToken;
import com.exploreMate.auth_service.model.UserAccount;
import com.exploreMate.auth_service.repo.AuthRepo;
import com.exploreMate.auth_service.repo.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepo repo;
    private final PasswordResetTokenRepository tokenRepo;
    private final AuthMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final KafkaTemplate<String, KafkaResDto> kalkaTemplet;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public SigninResDto signup(SignupReqDto signupReqDto) {
        if (repo.findByEmail(signupReqDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        UserAccount user = mapper.toEntity(signupReqDto);
        String displayName = (signupReqDto.getName() != null && !signupReqDto.getName().isBlank())
                ? signupReqDto.getName()
                : "Explorer";
        user.setName(displayName);
        user.setTitle("Explorer"); // Default title
        user.setPasswordHash(passwordEncoder.encode(signupReqDto.getPassword()));
        user.setRole("USER");
        // Generate a numeric ID based on timestamp + random
        user.setNumericId(System.currentTimeMillis() + (long) (Math.random() * 10000));

        UserAccount savedUser = repo.save(user);

        KafkaResDto body = KafkaResDto.builder()
                .email(user.getEmail())
                .subject("Account Activation and Welcome to ExploreMate")
                .body("<html><body style='margin: 0; padding: 0; background-color: #f4f4f4; font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;'><table width='100%' border='0' cellspacing='0' cellpadding='0' bgcolor='#f4f4f4'><tr><td align='center' style='padding: 40px 0;'><table width='600' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 4px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.05);'><tr><td><img src='https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=1200&q=80' alt='Himalayan Vista' width='600' style='display: block; width: 100%; max-width: 600px; height: auto;'></td></tr><tr><td style='padding: 40px 50px;'><h2 style='color: #1a1a1a; font-size: 24px; font-weight: 400; margin-top: 0;'>Welcome to ExploreMate</h2><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>Dear Member,</p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>It is a pleasure to confirm the successful registration of your account with <strong>ExploreMate</strong>. We are committed to providing an unparalleled planning experience, tailored to the complexities of modern travel and heritage exploration.</p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>Our platform serves as your central hub for itinerary optimization, offering high-fidelity insights into both established and emerging destinations within the Nepal region and beyond.</p><p style='padding: 20px 0; font-size: 16px;'><a href='https://exploremate.com/login' style='color: #004a99; text-decoration: none; border-bottom: 1px solid #004a99; padding-bottom: 2px;'>Proceed to Member Dashboard &rarr;</a></p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>Thank you for choosing ExploreMate. We look forward to facilitating your future expeditions.</p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6; margin-bottom: 0;'>Sincerely,<br><br><strong>The ExploreMate Executive Team</strong><br><span style='font-size: 13px; color: #888;'>Kathmandu | Nepal</span></p></td></tr><tr><td style='padding: 20px 50px; background-color: #fafafa; border-top: 1px solid #eeeeee;'><p style='font-size: 11px; color: #999; line-height: 1.5; margin: 0;'><strong>LEGAL DISCLAIMER:</strong> This communication is confidential and intended solely for the addressee. The information contained herein may be privileged and protected from disclosure. Unauthorized review, use, or distribution is strictly prohibited.</p></td></tr></table></td></tr></table></body></html>")
                .build();
        
        kalkaTemplet.send("signup", body);

        // Generate token for auto-login
        String token = jwtUtils.generateToken(savedUser.getEmail(), java.util.List.of(savedUser.getRole()));

        return SigninResDto.builder()
                .token(token)
                .userId(savedUser.getUserId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public SigninResDto signupAdmin(SignupReqDto signupReqDto) {
        if (repo.findByEmail(signupReqDto.getEmail()).isPresent()) {
            throw new RuntimeException("User Already present");
        }

        UserAccount user = mapper.toEntity(signupReqDto);
        user.setPasswordHash(passwordEncoder.encode(signupReqDto.getPassword()));
        user.setRole("ADMIN");
        user.setNumericId(System.currentTimeMillis() + (long) (Math.random() * 10000));

        UserAccount savedUser = repo.save(user);

        String token = jwtUtils.generateToken(savedUser.getEmail(), java.util.List.of(savedUser.getRole()));

        return SigninResDto.builder()
                .token(token)
                .userId(savedUser.getUserId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public SigninResDto signin(SigninReqDto signinReqDto) {
        UserAccount user = repo.findByEmail(signinReqDto.email())
                .orElseThrow(() -> new UsernameNotFoundException("Username or password didnot match"));
        if (!passwordEncoder.matches(signinReqDto.password(), user.getPasswordHash())) {
            throw new UsernameNotFoundException("Username or Password didnot match");

        }
        String role = user.getRole();
        return SigninResDto.builder()
                .token(jwtUtils.generateToken(signinReqDto.email(), java.util.List.of(role)))
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(role)
                .build();

    }

    public ProfileResponseDto updateProfile(String email, ProfileUpdateReqDto profileUpdateReqDto) {
        UserAccount user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Update only non-null fields
        if (profileUpdateReqDto.getName() != null && !profileUpdateReqDto.getName().isEmpty()) {
            user.setName(profileUpdateReqDto.getName());
        }
        if (profileUpdateReqDto.getPhoneNumber() != null) {
            user.setPhoneNumber(profileUpdateReqDto.getPhoneNumber());
        }
        if (profileUpdateReqDto.getProfilePicture() != null) {
            user.setProfilePicture(profileUpdateReqDto.getProfilePicture());
        }
        if (profileUpdateReqDto.getBio() != null) {
            user.setBio(profileUpdateReqDto.getBio());
        }
        if (profileUpdateReqDto.getTitle() != null) {
            user.setTitle(profileUpdateReqDto.getTitle());
        }
        if (profileUpdateReqDto.getLocation() != null) {
            user.setLocation(profileUpdateReqDto.getLocation());
        }
        if (profileUpdateReqDto.getDateOfBirth() != null) {
            user.setDateOfBirth(profileUpdateReqDto.getDateOfBirth());
        }
        if (profileUpdateReqDto.getInterests() != null) {
            user.setInterests(profileUpdateReqDto.getInterests());
        }
        if (profileUpdateReqDto.getBudget() != null) {
            user.setBudget(profileUpdateReqDto.getBudget());
        }
        if (profileUpdateReqDto.getTravelStyle() != null) {
            user.setTravelStyle(profileUpdateReqDto.getTravelStyle());
        }

        UserAccount updatedUser = repo.save(user);

        return ProfileResponseDto.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .name(updatedUser.getName())
                .role(updatedUser.getRole())
                .numericId(updatedUser.getNumericId())
                .phoneNumber(updatedUser.getPhoneNumber())
                .profilePicture(updatedUser.getProfilePicture())
                .bio(updatedUser.getBio())
                .title(updatedUser.getTitle())
                .location(updatedUser.getLocation())
                .dateOfBirth(updatedUser.getDateOfBirth())
                .interests(updatedUser.getInterests())
                .budget(updatedUser.getBudget())
                .travelStyle(updatedUser.getTravelStyle())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
    }

    public ProfileResponseDto getProfile(String email) {
        UserAccount user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .numericId(user.getNumericId())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .bio(user.getBio())
                .title(user.getTitle())
                .location(user.getLocation())
                .dateOfBirth(user.getDateOfBirth())
                .interests(user.getInterests())
                .budget(user.getBudget())
                .travelStyle(user.getTravelStyle())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public List<ProfileResponseDto> getAllUsers() {
        return repo.findAll().stream()
                .map(user -> ProfileResponseDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole())
                        .numericId(user.getNumericId())
                        .phoneNumber(user.getPhoneNumber())
                        .profilePicture(user.getProfilePicture())
                        .bio(user.getBio())
                        .location(user.getLocation())
                        .dateOfBirth(user.getDateOfBirth())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // Forgot Password - Send reset link via email
    public String forgotPassword(String email) {
        var userOpt = repo.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Don't reveal if email exists or not
            return "If the email exists, a reset link will be sent";
        }

        UserAccount user = userOpt.get();
        
        // Delete any existing tokens for this email
        tokenRepo.deleteByEmail(email);
        
        // Generate a secure reset token
        String resetToken = java.util.UUID.randomUUID().toString();
        
        // Save token with 1 hour expiry
        PasswordResetToken token = new PasswordResetToken(
            email, 
            resetToken, 
            java.time.LocalDateTime.now().plusHours(1)
        );
        tokenRepo.save(token);

        // Send email with reset link
        KafkaResDto emailBody = KafkaResDto.builder()
                .email(user.getEmail())
                .subject("Password Reset - ExploreMate")
                .body(buildPasswordResetEmail(user.getName(), resetToken))
                .build();

        kalkaTemplet.send("password-reset", emailBody)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    System.err.println("Failed to send Kafka message: " + ex.getMessage());
                } else {
                    System.err.println("Kafka message sent successfully to topic password-reset");
                }
            });

        return "If the email exists, a reset link will be sent";
    }

    // Reset Password
    public String resetPassword(String token, String newPassword) {
        var tokenOpt = tokenRepo.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            throw new RuntimeException("Invalid reset token");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.isExpired()) {
            tokenRepo.delete(resetToken);
            throw new RuntimeException("Reset token has expired");
        }
        
        // Find user and update password
        var userOpt = repo.findByEmail(resetToken.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        UserAccount user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        repo.save(user);
        
        // Delete the used token
        tokenRepo.delete(resetToken);
        
        return "Password reset successfully";
    }

    private String buildPasswordResetEmail(String name, String token) {
        return "<html><body style='font-family: Arial, sans-serif; padding: 20px;'><h2>Password Reset Request</h2><p>Hello " + name + ",</p><p>We received a request to reset your ExploreMate password.</p><p>Click the button below to reset your password:</p><a href='" + frontendUrl + "/reset-password?token=" + token + "' style='background-color: #008CBA; color: white; padding: 12px 20px; text-decoration: none; border-radius: 4px; display: inline-block; margin: 20px 0;'>Reset Password</a><p>Or copy this token: " + token + "</p><p>This link will expire in 1 hour.</p><p>If you didn't request this, please ignore this email.</p><p>Best regards,<br>ExploreMate Team</p></body></html>";
    }
}
