package com.HMS.hospital_management_system.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientResponseDto {

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
}
