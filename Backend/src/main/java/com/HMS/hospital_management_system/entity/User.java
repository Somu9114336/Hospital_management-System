package com.HMS.hospital_management_system.entity;

import com.HMS.hospital_management_system.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name is required")
    @Size(min = 4, message = "name must be 6 character long ")
    private String name;

    @NotBlank(message = "email required")
    @Email(message = "enter a valid email")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "phone number required")
    @Pattern(regexp = "^[0-9]{10}$", message = "phone number must be 10 digit")
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotBlank(message = "password required")
    private String password;


}
