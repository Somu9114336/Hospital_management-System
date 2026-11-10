package com.HMS.hospital_management_system.dto;

import com.HMS.hospital_management_system.enums.AppointmentStatus;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentResponseDto {

    private Long id;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;


    private LocalDate appointmentDate;


    private LocalTime startTime;


    private LocalTime endTime;


    private String reason;

    private AppointmentStatus status;


    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
