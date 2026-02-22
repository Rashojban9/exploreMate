package com.exploreMate.auth_service.controller;

import com.exploreMate.auth_service.dto.request.SigninReqDto;
import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.routes.AuthRoute;
import com.exploreMate.auth_service.service.AuthService;
import com.exploreMate.auth_service.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping(AuthRoute.SIGN_UP)
    public ResponseEntity<?> signUp(@RequestBody SignupReqDto signupReqDto) {
        return Response.sucess("sucessfully signup", service.signup(signupReqDto));
    }

    @PostMapping(AuthRoute.SIGN_UP_ALT)
    public ResponseEntity<?> register(@RequestBody SignupReqDto signupReqDto) {
        return Response.sucess("sucessfully registered", service.signup(signupReqDto));
    }

    @PostMapping(AuthRoute.SIGN_IN)
    public ResponseEntity<?> signIn(@RequestBody SigninReqDto signinReqDto) {
        return ResponseEntity.ok(service.signin(signinReqDto));
    }

    @GetMapping(AuthRoute.ME)
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.exploreMate.auth_service.model.UserAccount user) {
            return ResponseEntity.ok(Response.sucess("User info", java.util.Map.of(
                "userId", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole() != null ? user.getRole() : "USER"
            )));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.error("Not authenticated"));
    }

}
