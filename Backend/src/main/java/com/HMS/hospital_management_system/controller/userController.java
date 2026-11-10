package com.HMS.hospital_management_system.controller;

import com.HMS.hospital_management_system.dto.LoginRequestDto;
import com.HMS.hospital_management_system.dto.LoginResponseDto;
import com.HMS.hospital_management_system.dto.RegisterRequestDto;
import com.HMS.hospital_management_system.dto.RegisterResponseDto;
import com.HMS.hospital_management_system.service.UserService;
import com.HMS.hospital_management_system.serviceimpl.userServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class userController {
    private final UserService userService;

    public userController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto dto) {
        RegisterResponseDto res = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto>Login(@RequestBody @Valid LoginRequestDto dto){
        LoginResponseDto res=userService.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
