package com.exploreMate.auth_service.mapper;

import com.exploreMate.auth_service.dto.request.SignupReqDto;
import com.exploreMate.auth_service.dto.response.SigninResDto;
import com.exploreMate.auth_service.model.UserAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    UserAccount toEntity(SignupReqDto signupReqDto);
    SigninResDto toRes(UserAccount user);
}
