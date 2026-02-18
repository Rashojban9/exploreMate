package com.exploreMate.auth_service.controller;

import com.exploreMate.auth_service.dto.request.SigninReqDto;
import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.routes.AuthRoute;
import com.exploreMate.auth_service.service.AuthService;
import com.exploreMate.auth_service.utils.ApiResponse;
import com.exploreMate.auth_service.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    @PostMapping(AuthRoute.SIGN_UP)
    public ResponseEntity<?> signUp(@RequestBody SignupReqDto signupReqDto){
       return Response.sucess("sucessfully signup",service.signup(signupReqDto));
    }
    @PostMapping(AuthRoute.SIGN_IN)
    public ResponseEntity<?> signIn(@RequestBody SigninReqDto signinReqDto){
        return ResponseEntity.status(HttpStatus.OK).body(Response.sucess("Login successful",service.signin(signinReqDto)));
    }

}
