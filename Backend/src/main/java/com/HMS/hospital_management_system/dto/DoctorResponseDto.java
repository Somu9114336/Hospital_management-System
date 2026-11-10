package com.HMS.hospital_management_system.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DoctorResponseDto {
    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Doctor email is required")
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    @NotBlank(message = "Doctor specialization is required")
    private String specialization;

    private boolean active = true;
}
