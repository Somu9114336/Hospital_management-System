package com.HMS.hospital_management_system.dto;

import com.HMS.hospital_management_system.enums.Role;
import lombok.Data;

@Data
public class LoginResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private Role role;
    private String token;
}
