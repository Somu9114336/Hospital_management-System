package com.HMS.hospital_management_system.mapper;

import com.HMS.hospital_management_system.dto.PatientRequestDto;
import com.HMS.hospital_management_system.dto.PatientResponseDto;
import com.HMS.hospital_management_system.dto.UpdatePatientDto;
import com.HMS.hospital_management_system.entity.Patient;
import com.HMS.hospital_management_system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDto dto) {

        Patient patient = new Patient();

        patient.setGender(dto.getGender());
        patient.setDateOfBirth(dto.getDateOfBirth());


        return patient;
    }

    public PatientResponseDto toDto(Patient dto) {
        PatientResponseDto res = new PatientResponseDto();
        res.setEmail(dto.getUser().getEmail());
        res.setPhone(dto.getUser().getPhone());
        res.setName(dto.getUser().getName());
        res.setGender(dto.getGender());
        res.setDateOfBirth(dto.getDateOfBirth());

        return res;

    }

    public Patient mapToUpdatePatientEntity(UpdatePatientDto dto, Patient patient) {


        if (dto.getDateOfBirth() != null) {
            patient.setDateOfBirth(dto.getDateOfBirth());
        }

        if (dto.getGender() != null) {
            patient.setGender(dto.getGender());
        }

        return patient;
    }

}
