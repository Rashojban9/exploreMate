package com.exploreMate.auth_service.controller;

import com.exploreMate.auth_service.model.UserAccount;
import com.exploreMate.auth_service.repo.AuthRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class Test

{
    private final AuthRepo repo;
    @GetMapping("/test")
    public String test() {
        UserAccount user = UserAccount.builder()

                .name("Test User")
                .email("test@test.com")
                .passwordHash("123")
                .roles(Set.of("ROLE_USER"))
                .enabled(true)
                .locked(false)
                .build();

        repo.save(user);
        return "Saved";
    }

}
