package com.HMS.hospital_management_system.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequestDto {


    private LocalDate dateOfBirth;


    private String gender;

}
