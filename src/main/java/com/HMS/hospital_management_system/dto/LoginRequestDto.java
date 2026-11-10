package com.HMS.hospital_management_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @Email
    @NotBlank(message = "email required")
    private String email;

    @NotBlank(message = "password required")
    private String password;
}
