package com.HMS.hospital_management_system.service;

import com.HMS.hospital_management_system.dto.AppointmentRequestDto;
import com.HMS.hospital_management_system.dto.AppointmentResponseDto;

import java.util.List;


public interface AppointmentsService {
   AppointmentResponseDto bookAppointment(AppointmentRequestDto dto);
   List<AppointmentResponseDto>getAllAppointments();

   AppointmentResponseDto getAppointmentById(Long id);
   List<AppointmentResponseDto>getAllAppointmentsForAPatient(Long id);

   List<AppointmentResponseDto>getAllAppointmentsForADoctor(Long id);
   AppointmentResponseDto ResheduleAnAppointment(Long id,AppointmentRequestDto dto);

   AppointmentResponseDto cancelAnAppointment(Long id);
   String RemoveAnAppointment(Long id);

   List<AppointmentResponseDto> getMyAppointments();


}
