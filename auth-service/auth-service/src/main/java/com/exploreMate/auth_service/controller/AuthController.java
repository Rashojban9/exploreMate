package com.exploreMate.auth_service.controller;

import com.exploreMate.auth_service.dto.request.SigninReqDto;
import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.dto.request.ProfileUpdateReqDto;
import com.exploreMate.auth_service.dto.request.ForgotPasswordReqDto;
import com.exploreMate.auth_service.dto.request.ResetPasswordReqDto;
import com.exploreMate.auth_service.dto.request.AdminUserUpdateReqDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        System.out.println("Processing login request for: " + signinReqDto.email());
        try {
            return Response.sucess("Successfully signed in", service.signin(signinReqDto));
        } catch (Exception e) {
            System.err.println("Login error for " + signinReqDto.email() + ": " + e.getMessage());
            throw e; // Let GlobalExceptionHandler handle it
        }
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

    @PutMapping(AuthRoute.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestBody com.exploreMate.auth_service.dto.request.ChangePasswordReqDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = getEmailFromAuthentication(authentication);
        if (userEmail != null) {
            String result = service.changePassword(userEmail, request);
            return Response.sucess(result, null);
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

    @PutMapping(AuthRoute.ADMIN_USER_BY_ID)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody AdminUserUpdateReqDto dto) {
        return Response.sucess("User updated successfully", service.updateUser(id, dto));
    }

    @DeleteMapping(AuthRoute.ADMIN_USER_BY_ID)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        service.deleteUser(id);
        return Response.sucess("User deleted successfully", null);
    }

    @PutMapping(AuthRoute.ADMIN_RESET_PASSWORD)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> adminResetPassword(@PathVariable String id, @RequestBody java.util.Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Response.error("Password must be at least 6 characters"));
        }
        service.adminResetPassword(id, newPassword);
        return Response.sucess("Password reset successfully", null);
    }

    @GetMapping(AuthRoute.ADMIN_STATS)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAdminStats() {
        return Response.sucess("Admin stats retrieved successfully", service.getAdminStats());
    }

    @PostMapping(AuthRoute.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordReqDto request) {
        String result = service.forgotPassword(request.email());
        return Response.sucess(result, null);
    }

    @PostMapping(AuthRoute.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordReqDto request) {
        String result = service.resetPassword(request.token(), request.newPassword());
        return Response.sucess(result, null);
    }

}

