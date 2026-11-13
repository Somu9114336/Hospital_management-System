package com.HMS.hospital_management_system.serviceimpl;

import com.HMS.hospital_management_system.dto.AppointmentRequestDto;
import com.HMS.hospital_management_system.dto.AppointmentResponseDto;
import com.HMS.hospital_management_system.entity.Appointment;
import com.HMS.hospital_management_system.entity.Doctors;
import com.HMS.hospital_management_system.entity.Patient;
import com.HMS.hospital_management_system.enums.AppointmentStatus;
import com.HMS.hospital_management_system.exception.AppointmentNotFoundException;
import com.HMS.hospital_management_system.exception.DoctorNotFoundException;
import com.HMS.hospital_management_system.exception.PatientNotFoundException;
import com.HMS.hospital_management_system.mapper.AppointmentMapper;
import com.HMS.hospital_management_system.repositary.AppointmentRepository;
import com.HMS.hospital_management_system.repositary.DoctorRepository;
import com.HMS.hospital_management_system.repositary.PatientRepositary;
import com.HMS.hospital_management_system.service.AppointmentsService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentsService {
    private final AppointmentRepository appointmentRepositary;
    private final AppointmentMapper appointmentMapper;
    private final PatientRepositary patientRepositary;
    private final DoctorRepository doctorRepository;

    AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                           AppointmentMapper appointmentMapper,
                           PatientRepositary patientRepositary,
                           DoctorRepository doctorRepository) {

        this.appointmentRepositary = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.patientRepositary = patientRepositary;
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional
    public AppointmentResponseDto bookAppointment(AppointmentRequestDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        Patient patient = patientRepositary.findByUser_Email(email).orElseThrow(() -> new PatientNotFoundException("patient doesn`t exist"));

        Doctors doctors = doctorRepository.findById(dto.getDoctorId()).orElseThrow(() -> new DoctorNotFoundException("doctor not exists"));

        if (!doctors.isActive()) {
            throw new RuntimeException("doctor is inactive");
        }

        Appointment appointment = appointmentMapper.toEntity(dto);

        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new RuntimeException("Appointment end time must be after start time");
        }

        if (dto.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("appointment date must be in future");
        } else if (dto.getAppointmentDate().isEqual(LocalDate.now()) && !dto.getStartTime().isAfter(LocalTime.now())) {
            throw new RuntimeException("Appointment time must be in the future");

        }
        boolean ap = appointmentRepositary.hasOverlapping(patient.getId(), dto.getAppointmentDate(), dto.getStartTime(), dto.getEndTime());
        if (ap) {
            throw new RuntimeException("Patient already has an appointment during this time");
        }

        boolean isOverlapAppointment = appointmentRepositary.hasOverlappingAppointmentForBooking(dto.getDoctorId(), dto.getAppointmentDate(),
                dto.getStartTime(), dto.getEndTime());


        if (isOverlapAppointment) {
            throw new RuntimeException("Doctor already has an appointment during this time");
        }


        appointment.setPatient(patient);
        appointment.setDoctors(doctors);

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        LocalDateTime currentTime = LocalDateTime.now();
        appointment.setCreatedAt(currentTime);
        appointment.setUpdatedAt(currentTime);

        Appointment savedAppointment = appointmentRepositary.save(appointment);

        return appointmentMapper.toDto(savedAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAllAppointments() {
        List<Appointment> appointment = appointmentRepositary.findAll();
        if (appointment.isEmpty()) {
            throw new RuntimeException("no appointments to shows");
        }
        return appointment.stream().map(appointmentMapper::toDto).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {


        Appointment appointment = appointmentRepositary.findById(id)
                .orElseThrow(() ->
                        new AppointmentNotFoundException("no appointment found"));

        ensureAppointmentAccess(appointment);

        return appointmentMapper.toDto(appointment);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAllAppointmentsForAPatient(Long id) {

        List<Appointment> appointments = appointmentRepositary.findByPatient_Id(id);
        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("no appointments");
        }
        return appointments.stream().map(appointmentMapper::toDto).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAllAppointmentsForADoctor(Long id) {
        List<Appointment> appointments = appointmentRepositary.findByDoctors_Id(id);
        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("no appointments");
        }

        return appointments.stream().map(appointmentMapper::toDto).toList();
    }

    @Override
    @Transactional
    public AppointmentResponseDto ResheduleAnAppointment(Long id, AppointmentRequestDto dto) {

        Appointment appointment = appointmentRepositary.findById(id).orElseThrow(() -> new AppointmentNotFoundException("no appointment found to reshedule"));
        ensureAppointmentAccess(appointment);

        if (appointment.getStatus().equals(AppointmentStatus.CANCELLED)) {
            throw new RuntimeException("can not reshedule a cancle appointment");
        }
        if (appointment.getStatus().equals(AppointmentStatus.COMPLETED)) {
            throw new RuntimeException("can not reshedule a completed appointment");

        }
        Doctors doctors = doctorRepository.findById(dto.getDoctorId()).orElseThrow(() -> new RuntimeException("no doctor found"));

        if (!doctors.isActive()) {
            throw new RuntimeException("doctor is inactive");
        }
        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new RuntimeException("Appointment end time must be after start time");
        }

        if (dto.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("appointment date must be in future");
        } else if (dto.getAppointmentDate().isEqual(LocalDate.now()) && !dto.getStartTime().isAfter(LocalTime.now())) {
            throw new RuntimeException("Appointment time must be in the future");

        }

        boolean ap = appointmentRepositary.hasOverlappingDuringResheduling(appointment.getPatient().getId(), id, dto.getAppointmentDate(), dto.getStartTime(), dto.getEndTime());

        if (ap) {
            throw new RuntimeException("Patient already has an appointment during this time");
        }

        boolean isOverlapAppointment = appointmentRepositary.hasOverlappingAppointment(dto.getDoctorId(), id, dto.getAppointmentDate(),
                dto.getStartTime(), dto.getEndTime());

        if (isOverlapAppointment) {
            throw new RuntimeException("Doctor already has an appointment during this time");
        }

        appointment.setDoctors(doctors);
        appointment.setAppointmentDate(dto.getAppointmentDate());

        appointment.setStartTime(dto.getStartTime());
        appointment.setEndTime(dto.getEndTime());


        LocalDateTime currenTime = LocalDateTime.now();

        appointment.setUpdatedAt(currenTime);


        Appointment savedAppointment = appointmentRepositary.save(appointment);

        return appointmentMapper.toDto(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDto cancelAnAppointment(Long id) {
        Appointment appointment = appointmentRepositary.findById(id).orElseThrow(() -> new AppointmentNotFoundException("no appointmnt found"));
        ensureAppointmentAccess(appointment);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment already cancled");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Appointment already completed");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment saveAppointment = appointmentRepositary.save(appointment);


        return appointmentMapper.toDto(saveAppointment);
    }

    @Override
    @Transactional
    public String RemoveAnAppointment(Long id) {

        Appointment appointment = appointmentRepositary.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        appointmentRepositary.delete(appointment);

        return "Appointment removed successfully";
    }


    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getMyAppointments() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Patient patient = patientRepositary.findByUser_Email(email).orElseThrow(() -> new PatientNotFoundException("patient not found"));

        List<Appointment> appointments = appointmentRepositary.findByPatient_Id(patient.getId());

        return appointments.stream().map(appointmentMapper::toDto).toList();
    }

    private void ensureAppointmentAccess(Appointment appointment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        if (authentication == null
                || !authentication.getName().equals(appointment.getPatient().getUser().getEmail())) {
            throw new AccessDeniedException("You are not allowed to access this appointment");
        }
    }


}
