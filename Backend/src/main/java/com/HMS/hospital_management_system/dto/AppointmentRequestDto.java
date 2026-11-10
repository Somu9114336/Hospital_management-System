package com.HMS.hospital_management_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequestDto {

    private Long id;

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future(message = "appointment date must be in future")
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @NotBlank(message = "Reason for appointment is required")
    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}
