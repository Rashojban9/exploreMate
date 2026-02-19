package com.exploreMate.auth_service.service;

import com.exploreMate.auth_service.dto.request.SigninReqDto;
import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.dto.response.SigninResDto;
import com.exploreMate.auth_service.jwt.jwtUtils.JwtUtils;
import com.exploreMate.auth_service.kafka.KafkaResDto;
import com.exploreMate.auth_service.mapper.AuthMapper;
import com.exploreMate.auth_service.model.UserAccount;
import com.exploreMate.auth_service.repo.AuthRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepo repo;
    private final AuthMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final KafkaTemplate<String, KafkaResDto> kalkaTemplet;

    public String signup(SignupReqDto signupReqDto) {
        ;
        if (repo.findByEmail(signupReqDto.getEmail()).isPresent()) {
            throw new RuntimeException("User Already present");
        }

        UserAccount user = mapper.toEntity(signupReqDto);
        user.setPasswordHash(passwordEncoder.encode(signupReqDto.getPassword()));
        KafkaResDto body = KafkaResDto.builder().email(user.getEmail()).subject("Account Activation and Welcome to ExploreMate")

                .body("<html><body style='margin: 0; padding: 0; background-color: #f4f4f4; font-family: \\\"Helvetica Neue\\\", Helvetica, Arial, sans-serif;'><table width='100%' border='0' cellspacing='0' cellpadding='0' bgcolor='#f4f4f4'><tr><td align='center' style='padding: 40px 0;'><table width='600' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 4px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.05);'><tr><td><img src='https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=1200&q=80' alt='Himalayan Vista' width='600' style='display: block; width: 100%; max-width: 600px; height: auto;'></td></tr><tr><td style='padding: 40px 50px;'><h2 style='color: #1a1a1a; font-size: 24px; font-weight: 400; margin-top: 0;'>Welcome to ExploreMate</h2><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>Dear Member,</p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>It is a pleasure to confirm the successful registration of your account with <strong>ExploreMate</strong>. We are committed to providing an unparalleled planning experience, tailored to the complexities of modern travel and heritage exploration.</p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>Our platform serves as your central hub for itinerary optimization, offering high-fidelity insights into both established and emerging destinations within the Nepal region and beyond.</p><p style='padding: 20px 0; font-size: 16px;'><a href='https://exploremate.com/login' style='color: #004a99; text-decoration: none; border-bottom: 1px solid #004a99; padding-bottom: 2px;'>Proceed to Member Dashboard &rarr;</a></p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6;'>Thank you for choosing ExploreMate. We look forward to facilitating your future expeditions.</p><p style='font-size: 16px; color: #4a4a4a; line-height: 1.6; margin-bottom: 0;'>Sincerely,<br><br><strong>The ExploreMate Executive Team</strong><br><span style='font-size: 13px; color: #888;'>Kathmandu | Nepal</span></p></td></tr><tr><td style='padding: 20px 50px; background-color: #fafafa; border-top: 1px solid #eeeeee;'><p style='font-size: 11px; color: #999; line-height: 1.5; margin: 0;'><strong>LEGAL DISCLAIMER:</strong> This communication is confidential and intended solely for the addressee. The information contained herein may be privileged and protected from disclosure. Unauthorized review, use, or distribution is strictly prohibited.</p></td></tr></table></td></tr></table></body></html>").build();

        repo.save(user);

        kalkaTemplet.send("signup", body);
        return "";
    }

    public SigninResDto signin(SigninReqDto signinReqDto) {
        UserAccount user = repo.findByEmail(signinReqDto.email()).orElseThrow(() -> new UsernameNotFoundException("Username or password didnot match"));
        if (!passwordEncoder.matches(signinReqDto.password(), user.getPasswordHash())) {
            throw new UsernameNotFoundException("Username or Password didnot match");


        }
        return SigninResDto.builder().token(jwtUtils.generateToken(signinReqDto.email(), user.getRoles())).build();


    }
}
