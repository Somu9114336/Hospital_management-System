package com.HMS.hospital_management_system.controller;

import com.HMS.hospital_management_system.dto.AppointmentResponseDto;
import com.HMS.hospital_management_system.dto.PatientRequestDto;
import com.HMS.hospital_management_system.dto.PatientResponseDto;
import com.HMS.hospital_management_system.dto.UpdatePatientDto;
import com.HMS.hospital_management_system.service.PatientService;
import com.HMS.hospital_management_system.serviceimpl.PatientServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;

    PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

//    @PostMapping("/add")
//    public ResponseEntity<PatientResponseDto> addPatient(@Valid @RequestBody PatientRequestDto dto) {
//        PatientResponseDto res = patientService.addPatient(dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(res);
//    }

    @GetMapping("/get/{name}")
    public ResponseEntity<PatientResponseDto> getaPatient(@Valid @PathVariable("name") String name) {
        PatientResponseDto dto = patientService.getPatient(name);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<PatientResponseDto>> getAllPatient() {
        List<PatientResponseDto> dto = patientService.getAllPatient();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<PatientResponseDto> getaPatientById(@Valid @PathVariable Long id) {
        PatientResponseDto dto = patientService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(@PathVariable Long id, @RequestBody UpdatePatientDto dto) {
        PatientResponseDto res = patientService.updatePAtient(id, dto);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "patient delete successfully";
    }




}
