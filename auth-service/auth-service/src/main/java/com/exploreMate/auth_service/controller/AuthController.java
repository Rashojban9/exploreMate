package com.exploreMate.auth_service.controller;

import com.exploreMate.auth_service.dto.request.SigninReqDto;
import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.dto.request.ProfileUpdateReqDto;
import com.exploreMate.auth_service.routes.AuthRoute;
import com.exploreMate.auth_service.service.AuthService;
import com.exploreMate.auth_service.utils.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping({ AuthRoute.SIGN_UP, AuthRoute.SIGN_UP_ALT })
    public ResponseEntity<?> signUp(@RequestBody SignupReqDto signupReqDto) {
        return Response.sucess("successfully signup", service.signup(signupReqDto));
    }

    @PostMapping(AuthRoute.SIGN_IN)
    public ResponseEntity<?> signIn(@RequestBody SigninReqDto signinReqDto) {
        return Response.sucess("Successfully signed in", service.signin(signinReqDto));
    }

    @GetMapping(AuthRoute.ME)
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = getEmailFromAuthentication(authentication);

        if (email != null) {
            try {
                com.exploreMate.auth_service.dto.response.ProfileResponseDto profile = service.getProfile(email);
                java.util.Map<String, Object> userData = new java.util.HashMap<>();
                userData.put("userId", profile.getNumericId() != null ? profile.getNumericId() : 0L);
                userData.put("name", profile.getName() != null ? profile.getName() : "Explorer");
                userData.put("email", profile.getEmail());
                userData.put("role", profile.getRole() != null ? profile.getRole() : "USER");

                return Response.sucess("User info", userData);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("User not found"));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Not authenticated"));
    }

    @PutMapping(AuthRoute.PROFILE_UPDATE)
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateReqDto profileUpdateReqDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = getEmailFromAuthentication(authentication);
        if (userEmail != null) {
            return Response.sucess("Profile updated successfully",
                    service.updateProfile(userEmail, profileUpdateReqDto));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Not authenticated"));
    }

    @GetMapping(AuthRoute.PROFILE_UPDATE)
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = getEmailFromAuthentication(authentication);
        if (userEmail != null) {
            return Response.sucess("Profile retrieved successfully", service.getProfile(userEmail));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Not authenticated"));
    }

    private String getEmailFromAuthentication(Authentication authentication) {
        if (authentication == null)
            return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.exploreMate.auth_service.model.UserAccount user) {
            return user.getEmail();
        } else if (principal instanceof String s) {
            return s;
        }
        return null;
    }

    // ─── Admin Endpoints ──────────────────────────────────────────────────

    @PostMapping(AuthRoute.ADMIN_SIGN_UP)
    public ResponseEntity<?> signUpAdmin(@RequestBody SignupReqDto signupReqDto) {
        return Response.sucess("Admin successfully signed up", service.signupAdmin(signupReqDto));
    }

    @GetMapping(AuthRoute.ADMIN_USERS)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return Response.sucess("All users retrieved successfully", service.getAllUsers());
    }

}
