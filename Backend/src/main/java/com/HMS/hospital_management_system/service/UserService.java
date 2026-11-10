package com.HMS.hospital_management_system.service;

import com.HMS.hospital_management_system.dto.LoginRequestDto;
import com.HMS.hospital_management_system.dto.LoginResponseDto;
import com.HMS.hospital_management_system.dto.RegisterRequestDto;
import com.HMS.hospital_management_system.dto.RegisterResponseDto;

public interface UserService {

    RegisterResponseDto registerUser(RegisterRequestDto dto);
    LoginResponseDto login(LoginRequestDto dto);
}
