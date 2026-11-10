package com.HMS.hospital_management_system.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequestDto {

    @NotBlank(message = "Patient name required")
    private String name;

    @NotBlank(message = "email required")
    @Email(message = "enter a valid email ")
    @Column(unique = true,nullable = false)
    private String email;

    @NotBlank(message = "phone number required")
    @Pattern(regexp = "^[0-9]{10}$",message = "phone number must be 10 digites")
    private String phone;

    @Past(message = "date of birth must be in past")

    private LocalDate dateOfBirth;

    @NotBlank(message = "gender required")
    private String gender;

    @NotBlank(message = "password is requierd")
    @Size(min = 8,max=14, message = "Password must be at least 8 characters")
    private String password;
}
