package com.HMS.hospital_management_system.repositary;

import com.HMS.hospital_management_system.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepositary extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser_Email(String email);

    Optional<Patient>findByUser_Name(String name);


}
