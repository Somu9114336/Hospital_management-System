package com.HMS.hospital_management_system.dto;

import com.HMS.hospital_management_system.entity.Appointment;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SameDayAppointmentValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void requestAllowsAppointmentDateToday() {
        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setDoctorId(1L);
        request.setAppointmentDate(LocalDate.now());
        request.setReason("Consultation");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void entityAllowsAppointmentDateToday() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setReason("Consultation");

        assertTrue(validator.validate(appointment).isEmpty());
    }
}
