package com.HMS.hospital_management_system.dto;

import com.HMS.hospital_management_system.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterResponseDto {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private Role role;


}
