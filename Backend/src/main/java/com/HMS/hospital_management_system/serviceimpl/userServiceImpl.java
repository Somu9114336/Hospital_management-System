package com.HMS.hospital_management_system.serviceimpl;

import com.HMS.hospital_management_system.config.JwtService;
import com.HMS.hospital_management_system.dto.LoginRequestDto;
import com.HMS.hospital_management_system.dto.LoginResponseDto;
import com.HMS.hospital_management_system.dto.RegisterRequestDto;
import com.HMS.hospital_management_system.dto.RegisterResponseDto;
import com.HMS.hospital_management_system.entity.Patient;
import com.HMS.hospital_management_system.entity.User;
import com.HMS.hospital_management_system.enums.Role;
import com.HMS.hospital_management_system.repositary.PatientRepositary;
import com.HMS.hospital_management_system.repositary.UserRepositary;
import com.HMS.hospital_management_system.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class userServiceImpl implements UserService {

    private final UserRepositary userRepositary;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PatientRepositary patientRepositary;

    public userServiceImpl(UserRepositary userRepositary, PatientRepositary patientRepositary, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepositary = userRepositary;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.patientRepositary = patientRepositary;
    }


    private User mapToEntity(RegisterRequestDto req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhone(req.getPhone());

        String hashedPassword = passwordEncoder.encode(req.getPassword());
        user.setPassword(hashedPassword);

        user.setRole(Role.PATIENT);

        return user;
    }

    private RegisterResponseDto mapToDto(User user) {
        RegisterResponseDto dto = new RegisterResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }

    private LoginResponseDto mapToDtoForLogin(User user) {
        LoginResponseDto dto = new LoginResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());

        return dto;
    }


    @Override
    public RegisterResponseDto registerUser(RegisterRequestDto dto) {
        userRepositary.findByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException("Email already exists");
                });

        User user =userRepositary.save( mapToEntity(dto));
        Patient patient = new Patient();
        patient.setUser(user);
        patientRepositary.save(patient);
        return mapToDto(user);

    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepositary.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("user doesnt exists"));
        boolean isCorrectPassword = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        if (!isCorrectPassword) {
            throw new RuntimeException("incorrect email or password");
        }
        String token = jwtService.generateToken(dto.getEmail());

        LoginResponseDto loginDto = mapToDtoForLogin(user);
        loginDto.setToken(token);

        return loginDto;
    }
}
