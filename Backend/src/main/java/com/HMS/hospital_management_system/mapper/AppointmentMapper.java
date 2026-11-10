package com.HMS.hospital_management_system.mapper;

import com.HMS.hospital_management_system.dto.AppointmentRequestDto;
import com.HMS.hospital_management_system.dto.AppointmentResponseDto;
import com.HMS.hospital_management_system.entity.Appointment;
import com.HMS.hospital_management_system.entity.Doctors;
import com.HMS.hospital_management_system.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDto dto) {

        Appointment appointment = new Appointment();

        Patient patient = new Patient();
        patient.setId(dto.getPatientId());

        Doctors doctor = new Doctors();
        doctor.setId(dto.getDoctorId());

        appointment.setPatient(patient);
        appointment.setDoctors(doctor);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());
        appointment.setReason(dto.getReason());
        appointment.setNotes(dto.getNotes());

        return appointment;

    }

    public AppointmentResponseDto toDto(Appointment appointment) {

        AppointmentResponseDto dto = new AppointmentResponseDto();

        dto.setPatientId(appointment.getPatient().getId());
        dto.setDoctorId(appointment.getDoctors().getId());
        dto.setPatientName(appointment.getPatient().getName());
        dto.setDoctorName(appointment.getDoctors().getName());
        dto.setId(appointment.getId());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setStatus(appointment.getStatus());
        dto.setReason(appointment.getReason());
        dto.setNotes(appointment.getNotes());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }
}
