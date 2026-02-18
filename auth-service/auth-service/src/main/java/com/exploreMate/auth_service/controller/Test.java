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

    @GetMapping("/test")
    public String test() {

        return "Saved";
    }

}
