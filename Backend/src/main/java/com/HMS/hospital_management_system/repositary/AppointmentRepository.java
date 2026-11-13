package com.HMS.hospital_management_system.repositary;

import com.HMS.hospital_management_system.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient_Id(Long id);

    List<Appointment> findByDoctors_Id(Long id);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +

            "WHERE a.doctors.id = :doctorId " +
            "AND a.id <> :id " +
            "AND a.appointmentDate = :date " +
            "AND a.status <> com.HMS.hospital_management_system.enums.AppointmentStatus.CANCELLED " +
            "AND a.startTime < :newEndTime " +
            "AND a.endTime > :newStartTime")
    boolean hasOverlappingAppointment(
            @Param("doctorId") Long doctorId,
            @Param("id") Long id,
            @Param("date") LocalDate date,
            @Param("newStartTime") LocalTime newStartTime,
            @Param("newEndTime") LocalTime newEndTime
    );

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +

            "WHERE a.doctors.id = :doctorId " +
            "AND a.appointmentDate = :date " +
            "AND a.status <> com.HMS.hospital_management_system.enums.AppointmentStatus.CANCELLED " +
            "AND a.startTime < :newEndTime " +
            "AND a.endTime > :newStartTime")
    boolean hasOverlappingAppointmentForBooking(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("newStartTime") LocalTime newStartTime,
            @Param("newEndTime") LocalTime newEndTime
    );

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.appointmentDate = :date " +
            "AND a.status <> com.HMS.hospital_management_system.enums.AppointmentStatus.CANCELLED " +
            "AND a.startTime < :endTime " +
            "AND a.endTime > :startTime")
    boolean hasOverlapping(
            @Param("patientId") Long patientId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime StartTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.patient.id=:patientId " +
            "AND a.id <> :id " +
            "AND a.appointmentDate=:date " +
            "AND a.status <> com.HMS.hospital_management_system.enums.AppointmentStatus.CANCELLED " +
            "AND  a.startTime < :EndTime " +
            "AND a.endTime > :StartTime")
    boolean hasOverlappingDuringResheduling(
            @Param("patientId") Long patientId,
            @Param("id") long id,
            @Param("date") LocalDate date,
            @Param("StartTime") LocalTime StartTime,
            @Param("EndTime") LocalTime EndTime
    );
}
