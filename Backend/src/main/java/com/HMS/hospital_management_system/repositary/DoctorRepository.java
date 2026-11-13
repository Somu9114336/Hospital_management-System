package com.HMS.hospital_management_system.repositary;

import com.HMS.hospital_management_system.entity.Doctors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctors, Long> {
    Optional<Doctors> findByEmail(String email);

    List<Doctors> findBySpecialization(String specialization);
    List<Doctors>findByName(String name);


}
