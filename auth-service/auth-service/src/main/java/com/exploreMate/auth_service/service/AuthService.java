package com.exploreMate.auth_service.service;

import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.mapper.AuthMapper;
import com.exploreMate.auth_service.repo.AuthRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepo repo;
    private final AuthMapper mapper;

    public String signup(SignupReqDto signupReqDto) {
        repo.save(mapper.toEntity(signupReqDto));
        return"";
    }
}
