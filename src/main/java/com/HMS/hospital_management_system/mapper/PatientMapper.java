package com.HMS.hospital_management_system.mapper;

import com.HMS.hospital_management_system.dto.PatientRequestDto;
import com.HMS.hospital_management_system.dto.PatientResponseDto;
import com.HMS.hospital_management_system.dto.UpdatePatientDto;
import com.HMS.hospital_management_system.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDto dto) {

        Patient patient = new Patient();

        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setGender(dto.getGender());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setPhone(dto.getPhone());

        return patient;
    }

    public PatientResponseDto toDto(Patient dto) {
        PatientResponseDto res = new PatientResponseDto();
        res.setName(dto.getName());
        res.setEmail(dto.getEmail());
        res.setPhone(dto.getPhone());
        res.setGender(dto.getGender());
        res.setDateOfBirth(dto.getDateOfBirth());

        return res;

    }

    public Patient mapToUpdatePatientEntity(UpdatePatientDto dto,Patient patient) {


        if (dto.getName() != null) {
            patient.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            patient.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            patient.setPhone(dto.getPhone());
        }

        if (dto.getDateOfBirth() != null) {
            patient.setDateOfBirth(dto.getDateOfBirth());
        }

        if (dto.getGender() != null) {
            patient.setGender(dto.getGender());
        }

        return patient;
    }

}
