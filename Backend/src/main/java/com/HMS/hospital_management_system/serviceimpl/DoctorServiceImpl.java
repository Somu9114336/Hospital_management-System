package com.HMS.hospital_management_system.serviceimpl;

import com.HMS.hospital_management_system.dto.DoctorRequestDto;
import com.HMS.hospital_management_system.dto.DoctorResponseDto;
import com.HMS.hospital_management_system.dto.UpdateDoctorDto;
import com.HMS.hospital_management_system.entity.Doctors;
import com.HMS.hospital_management_system.exception.DoctorNotFoundException;
import com.HMS.hospital_management_system.mapper.DoctorMapper;
import com.HMS.hospital_management_system.repositary.DoctorRepository;
import com.HMS.hospital_management_system.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }


    @Override
    public DoctorResponseDto addDoctor(DoctorRequestDto dto) {
        if (doctorRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("doctor exists in DB");
        }
        Doctors saveDoctors = doctorRepository.save(doctorMapper.toEntity(dto));
        return doctorMapper.toDto(saveDoctors);
    }

    @Override
    public DoctorResponseDto getById(Long id) {
        Doctors doctors = doctorRepository.findById(id).orElseThrow(() ->
                new DoctorNotFoundException("doctor not found with this id"));
        return doctorMapper.toDto(doctors);
    }

    @Override
    public List<DoctorResponseDto> getAllDoctors() {
        List<Doctors> doctors = doctorRepository.findAll();
        if (doctors.isEmpty()) {
            throw new DoctorNotFoundException("no doctor found");
        }

        return doctors.stream().map(doctorMapper::toDto).toList();
    }

    @Override
    public DoctorResponseDto updateDoctor(DoctorRequestDto dto, Long id) {
        Doctors doctors = doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException("not found"));

        Doctors updateDoctors = doctorMapper.updateDoctorMapperToEntity(dto, doctors);
        Doctors saveDoctors = doctorRepository.save(updateDoctors);

        return doctorMapper.toDto(saveDoctors);
    }

    @Override
    public DoctorResponseDto updateSingleField(Long id, UpdateDoctorDto dto) {
        Doctors doctors = doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException("not found"));

        Doctors updateDoctors = doctorMapper.updateaDoctorMapperToEntity(dto, doctors);
        Doctors saveDoctors = doctorRepository.save(updateDoctors);


        return doctorMapper.toDto(saveDoctors);
    }

    @Override
    public void deletedoctor(Long id) {
        doctorRepository.deleteById(id);

    }

    @Override
    public List<DoctorResponseDto> getBySpecialization(String specialization) {
        List<Doctors> doctors=doctorRepository.findBySpecialization(specialization);
        if(doctors.isEmpty()){
            throw new DoctorNotFoundException(" no doctor found");
        }
        return doctors.stream().map(doctorMapper::toDto).toList();
    }
}
