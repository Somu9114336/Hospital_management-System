package com.HMS.hospital_management_system.controller;

import com.HMS.hospital_management_system.dto.AppointmentRequestDto;
import com.HMS.hospital_management_system.dto.AppointmentResponseDto;
import com.HMS.hospital_management_system.service.AppointmentsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentsService appointmentsService;

    AppointmentController(AppointmentsService appointmentsService) {
        this.appointmentsService = appointmentsService;
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponseDto> bookAppointment(@Valid @RequestBody AppointmentRequestDto dto) {
        AppointmentResponseDto res = appointmentsService.bookAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        List<AppointmentResponseDto> res = appointmentsService.getAllAppointments();
        return ResponseEntity.ok().body(res);

    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AppointmentResponseDto> getById(@PathVariable Long id) {
        AppointmentResponseDto res = appointmentsService.getAppointmentById(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get/myAppointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointmentsForPatient(@PathVariable Long id) {
        List<AppointmentResponseDto> res = appointmentsService.getAllAppointmentsForAPatient(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get/doctor/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentForADoctor(@PathVariable Long id) {
        List<AppointmentResponseDto> res = appointmentsService.getAllAppointmentsForADoctor(id);
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("/reshedule/{id}")
    public ResponseEntity<AppointmentResponseDto> resheduleAnAppointment(@PathVariable Long id,
                                                                         @RequestBody @Valid AppointmentRequestDto dto) {
        AppointmentResponseDto res = appointmentsService.ResheduleAnAppointment(id, dto);
        return ResponseEntity.ok().body(res);
    }

    @PatchMapping("/cancle/{id}")
    public ResponseEntity<AppointmentResponseDto> cancelAnAppointment(@PathVariable Long id) {
        AppointmentResponseDto res = appointmentsService.cancelAnAppointment(id);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAnAppointment(@PathVariable Long id) {
        String res = appointmentsService.RemoveAnAppointment(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/getallmyappointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllMyAppointments() {

        List<AppointmentResponseDto> res = appointmentsService.getMyAppointments();
        return ResponseEntity.ok().body(res);
    }
}
