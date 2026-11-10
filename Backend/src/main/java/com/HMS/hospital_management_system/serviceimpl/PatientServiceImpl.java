package com.HMS.hospital_management_system.serviceimpl;

import com.HMS.hospital_management_system.dto.AppointmentResponseDto;
import com.HMS.hospital_management_system.dto.PatientRequestDto;
import com.HMS.hospital_management_system.dto.PatientResponseDto;
import com.HMS.hospital_management_system.dto.UpdatePatientDto;
import com.HMS.hospital_management_system.entity.Appointment;
import com.HMS.hospital_management_system.entity.Patient;
import com.HMS.hospital_management_system.exception.PatientNotFoundException;
import com.HMS.hospital_management_system.mapper.AppointmentMapper;
import com.HMS.hospital_management_system.mapper.PatientMapper;
import com.HMS.hospital_management_system.repositary.AppointmentRepository;
import com.HMS.hospital_management_system.repositary.PatientRepositary;
import com.HMS.hospital_management_system.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {
    private PatientRepositary patientRepositary;
    private PatientMapper patientMapper;
    private AppointmentRepository appointmentRepository;
    private AppointmentMapper appointmentMapper;

    PatientServiceImpl(PatientRepositary patientRepositary, PatientMapper patientMapper,
                       AppointmentRepository appointmentRepository,AppointmentMapper appointmentMapper) {
        this.patientRepositary = patientRepositary;
        this.patientMapper = patientMapper;
        this.appointmentRepository=appointmentRepository;
        this.appointmentMapper=appointmentMapper;
    }


    public PatientResponseDto addPatient(PatientRequestDto dto) {
        if (patientRepositary.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("patient altreday exists");
        }
        Patient savedPatient = patientRepositary.save(patientMapper.toEntity(dto));

        return patientMapper.toDto(savedPatient);
    }

    @Override
    public PatientResponseDto getPatient(String name) {
        Patient patient = patientRepositary.findByName(name).orElseThrow(() -> new PatientNotFoundException("patient does not exists"));

        return patientMapper.toDto(patient);
    }

    @Override
    public List<PatientResponseDto> getAllPatient() {
        List<Patient> patient = patientRepositary.findAll();
        if (patient.isEmpty()) {
            throw new PatientNotFoundException("no patient found");
        }
        return patient.stream().map(patientMapper::toDto).toList();
    }

    @Override
    public PatientResponseDto getById(Long id) {
        Patient patient = patientRepositary.findById(id).orElseThrow(() -> new PatientNotFoundException("no patient found with this id"));
        return patientMapper.toDto(patient);
    }

    @Override
    public PatientResponseDto updatePAtient(Long id, UpdatePatientDto dto) {

        Patient patient = patientRepositary.findById(id).orElseThrow(() -> new PatientNotFoundException("no patient found with this id"));

        Patient savedPatient = patientRepositary.save(patientMapper.mapToUpdatePatientEntity(dto, patient));

        return patientMapper.toDto(savedPatient);
    }

    @Override
    public String deletePatient(Long id) {
        patientRepositary.deleteById(id);
        return "patient deleted";
    }

}
