package com.exploreMate.auth_service.dto.request;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupReqDto {
    private String email;
    private String name;
    private String password;

}
