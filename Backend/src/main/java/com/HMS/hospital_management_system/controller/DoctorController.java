package com.HMS.hospital_management_system.controller;

import com.HMS.hospital_management_system.dto.DoctorRequestDto;
import com.HMS.hospital_management_system.dto.DoctorResponseDto;
import com.HMS.hospital_management_system.dto.UpdateDoctorDto;
import com.HMS.hospital_management_system.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/add")
    public ResponseEntity<DoctorResponseDto> addDoctor(@RequestBody @Valid DoctorRequestDto dto) {
        DoctorResponseDto res = doctorService.addDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<DoctorResponseDto> getById(@PathVariable Long id) {
        DoctorResponseDto res = doctorService.getById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(res);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctprs() {
        List<DoctorResponseDto> res = doctorService.getAllDoctors();
        return ResponseEntity.status(HttpStatus.FOUND).body(res);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorResponseDto>> getBySpecialization(@PathVariable String specialization) {
        List<DoctorResponseDto> res = doctorService.getBySpecialization(specialization);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctor(@RequestBody @Valid DoctorRequestDto dto,
                                                          @PathVariable Long id) {
        DoctorResponseDto res = doctorService.updateDoctor(dto, id);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("updatesingle/{id}")
    public ResponseEntity<DoctorResponseDto> UpdateFields(@PathVariable Long id, @RequestBody @Valid UpdateDoctorDto dto) {
        DoctorResponseDto res = doctorService.updateSingleField(id, dto);

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> removeDoctor(@PathVariable Long id) {
        doctorService.deletedoctor(id);
        return ResponseEntity.status(HttpStatus.OK).body("delete success");
    }

    @GetMapping("/getbyname/{name}")
    public ResponseEntity<List<DoctorResponseDto>> getDoctorByName(@PathVariable String name) {
        List<DoctorResponseDto> res = doctorService.getByname(name);
        return ResponseEntity.ok().body(res);
    }
}
