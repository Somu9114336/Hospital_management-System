package com.HMS.hospital_management_system.mapper;

import com.HMS.hospital_management_system.dto.DoctorRequestDto;
import com.HMS.hospital_management_system.dto.DoctorResponseDto;
import com.HMS.hospital_management_system.dto.UpdateDoctorDto;
import com.HMS.hospital_management_system.entity.Doctors;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {
    public Doctors toEntity(DoctorRequestDto dto) {

        Doctors doctors = new Doctors();

        doctors.setPhone(dto.getPhone());
        doctors.setName(dto.getName());
        doctors.setEmail(dto.getEmail());
        doctors.setSpecialization(dto.getSpecialization());

        return doctors;
    }

    public DoctorResponseDto toDto(Doctors doctors) {

        DoctorResponseDto dto = new DoctorResponseDto();

        dto.setName(doctors.getName());
        dto.setEmail(doctors.getEmail());
        dto.setSpecialization(doctors.getSpecialization());
        dto.setPhone(doctors.getPhone());

        return dto;
    }

    public Doctors updateDoctorMapperToEntity(DoctorRequestDto dto, Doctors doctors) {

        doctors.setName(dto.getName());
        doctors.setEmail(dto.getEmail());
        doctors.setPhone(dto.getPhone());
        doctors.setSpecialization(dto.getSpecialization());
        return doctors;
    }

    public Doctors updateaDoctorMapperToEntity(UpdateDoctorDto dto, Doctors doctors) {

        if (dto.getName() != null) {
            doctors.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            doctors.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            doctors.setPhone(dto.getPhone());
        }

        if (dto.getSpecialization() != null) {
            doctors.setSpecialization(dto.getSpecialization());
        }

        return doctors;
    }


}
