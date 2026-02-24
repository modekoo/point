package com.point.domain.controller;

import com.point.domain.dto.user.CreateUserReqDto;
import com.point.domain.service.UserService;
import com.point.dto.CommonResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<CommonResponseDto> createUser(@RequestBody @Valid CreateUserReqDto createUserReqDTO){
        userService.createUser(createUserReqDTO);
        return ResponseEntity.ok(CommonResponseDto.success());
    }

}
