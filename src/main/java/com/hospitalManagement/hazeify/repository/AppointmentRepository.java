package com.hospitalManagement.hazeify.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hospitalManagement.hazeify.entity.Appointment;
import com.hospitalManagement.hazeify.entity.Appointment.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        // Required methods
        List<Appointment> findByDoctorId(Long doctorId);

        List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.patient.id = :patientId")
        List<Appointment> findByPatientId(@Param("patientId") Long patientId);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.doctor.id = :doctorId")
        List<Appointment> findByDoctorIdWithRelations(@Param("doctorId") Long doctorId);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.doctor.id = :doctorId AND a.status = :status")
        List<Appointment> findByDoctorIdAndStatus(@Param("doctorId") Long doctorId,
                        @Param("status") AppointmentStatus status);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.doctor.id = :doctorId AND a.date >= :startDate ORDER BY a.date, a.time")
        List<Appointment> findUpcomingAppointmentsByDoctor(@Param("doctorId") Long doctorId,
                        @Param("startDate") LocalDate startDate);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.patient.id = :patientId AND a.date >= :startDate ORDER BY a.date, a.time")
        List<Appointment> findUpcomingAppointmentsByPatient(@Param("patientId") Long patientId,
                        @Param("startDate") LocalDate startDate);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date AND a.time = :time AND a.status IN ('PENDING', 'CONFIRMED')")
        long countConflictingAppointments(@Param("doctorId") Long doctorId, @Param("date") LocalDate date,
                        @Param("time") LocalDateTime time);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient WHERE a.status = :status")
        List<Appointment> findByStatus(@Param("status") AppointmentStatus status);

        @Query("SELECT a FROM Appointment a JOIN FETCH a.doctor JOIN FETCH a.patient ORDER BY a.date DESC")
        List<Appointment> findAllWithRelations();
}