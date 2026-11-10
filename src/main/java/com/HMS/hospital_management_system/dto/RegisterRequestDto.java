package com.HMS.hospital_management_system.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "name is required")
    @Size(min = 4, message = "name must be 6 character long ")
    private String name;

    @NotBlank(message = "email required")
    @Email(message = "enter a valid email")
    private String email;

    @NotBlank(message = "phone number required")
    @Pattern(regexp = "^[0-9]{10}$", message = "phone number must be 10 digit")
    private String phone;

    @NotBlank(message = "password is required")
    @Size(min = 5, max = 10, message = "password must be in between 5 to 10 character long")
    private String password;
}
