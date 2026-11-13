package com.HMS.hospital_management_system.service;

import com.HMS.hospital_management_system.dto.DoctorRequestDto;
import com.HMS.hospital_management_system.dto.DoctorResponseDto;
import com.HMS.hospital_management_system.dto.UpdateDoctorDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DoctorService {
    public DoctorResponseDto addDoctor(DoctorRequestDto dto);

   public DoctorResponseDto getById(Long id);

   public List<DoctorResponseDto> getAllDoctors();

    DoctorResponseDto updateDoctor(DoctorRequestDto dto,Long id);

    DoctorResponseDto updateSingleField(Long id, UpdateDoctorDto dto);

    void deletedoctor(Long id);

    List<DoctorResponseDto> getBySpecialization(String specialization);
    List<DoctorResponseDto>getByname(String name);
}
