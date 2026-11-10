package com.HMS.hospital_management_system.service;

import com.HMS.hospital_management_system.dto.AppointmentResponseDto;
import com.HMS.hospital_management_system.dto.PatientRequestDto;
import com.HMS.hospital_management_system.dto.PatientResponseDto;
import com.HMS.hospital_management_system.dto.UpdatePatientDto;

import java.util.List;

public interface PatientService {
     PatientResponseDto addPatient(PatientRequestDto dto);
     PatientResponseDto getPatient(String name);
     List<PatientResponseDto>getAllPatient();
     PatientResponseDto getById(Long id);
     PatientResponseDto updatePAtient(Long id, UpdatePatientDto dto);
     String deletePatient(Long id);

}
