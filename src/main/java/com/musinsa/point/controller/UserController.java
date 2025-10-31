package com.musinsa.point.controller;

import com.musinsa.point.dto.ReqCreateUserDTO;
import com.musinsa.point.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public <T> ResponseEntity<T> createUser(@RequestBody @Valid ReqCreateUserDTO reqCreateUserDTO){
        userService.createUser(reqCreateUserDTO);
        return ResponseEntity.ok().build();
    }

}
