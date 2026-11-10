package com.HMS.hospital_management_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateDoctorDto {

    private String name;

    @Email
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;


    private String specialization;

    private boolean active = true;
}
