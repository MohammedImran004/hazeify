package com.hospitalManagement.hazeify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hospitalManagement.hazeify.entity.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Standard JPA methods
    List<Doctor> findAll();

    Optional<Doctor> findById(Long id);

    Doctor save(Doctor doctor);

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();

    // Existing custom methods
    Optional<Doctor> findByEmail(String email);

    List<Doctor> findByIsAvailableTrue();

    List<Doctor> findBySpecialization(String specialization);

    @Query("SELECT d FROM Doctor d WHERE d.isAvailable = true ORDER BY d.name")
    List<Doctor> findAllAvailableDoctors();

    boolean existsByEmail(String email);
}