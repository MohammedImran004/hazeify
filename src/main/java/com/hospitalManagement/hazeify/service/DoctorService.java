package com.hospitalManagement.hazeify.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalManagement.hazeify.dto.DoctorDto;
import com.hospitalManagement.hazeify.entity.Doctor;
import com.hospitalManagement.hazeify.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public Doctor createDoctor(DoctorDto doctorDto) {
        if (doctorRepository.existsByEmail(doctorDto.getEmail())) {
            throw new RuntimeException("Doctor with this email already exists");
        }

        Doctor doctor = new Doctor();
        doctor.setName(doctorDto.getName());
        doctor.setSpecialization(doctorDto.getSpecialization());
        doctor.setEmail(doctorDto.getEmail());
        doctor.setPhoneNumber(doctorDto.getPhoneNumber());
        doctor.setDescription(doctorDto.getDescription());
        doctor.setVisitingStartTime(doctorDto.getVisitingStartTime());
        doctor.setVisitingEndTime(doctorDto.getVisitingEndTime());
        doctor.setConsultationFee(doctorDto.getConsultationFee());
        doctor.setAvailable(doctorDto.isAvailable());

        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, DoctorDto doctorDto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check if email is being changed and if it already exists
        if (!doctor.getEmail().equals(doctorDto.getEmail()) &&
                doctorRepository.existsByEmail(doctorDto.getEmail())) {
            throw new RuntimeException("Doctor with this email already exists");
        }

        doctor.setName(doctorDto.getName());
        doctor.setSpecialization(doctorDto.getSpecialization());
        doctor.setEmail(doctorDto.getEmail());
        doctor.setPhoneNumber(doctorDto.getPhoneNumber());
        doctor.setDescription(doctorDto.getDescription());
        doctor.setVisitingStartTime(doctorDto.getVisitingStartTime());
        doctor.setVisitingEndTime(doctorDto.getVisitingEndTime());
        doctor.setConsultationFee(doctorDto.getConsultationFee());
        doctor.setAvailable(doctorDto.isAvailable());

        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorRepository.findAll();
            System.out.println("DoctorService: Found " + doctors.size() + " total doctors");
            return doctors;
        } catch (Exception e) {
            System.err.println("DoctorService Error: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public List<Doctor> getAvailableDoctors() {
        try {
            List<Doctor> doctors = doctorRepository.findAllAvailableDoctors();
            System.out.println("DoctorService: Found " + doctors.size() + " available doctors");
            return doctors;
        } catch (Exception e) {
            System.err.println("DoctorService Error: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElse(null);
    }

    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found");
        }
        doctorRepository.deleteById(id);
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    public Doctor updateDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

}