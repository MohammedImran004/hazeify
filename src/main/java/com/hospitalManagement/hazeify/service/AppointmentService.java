package com.hospitalManagement.hazeify.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hospitalManagement.hazeify.dto.AppointmentDto;
import com.hospitalManagement.hazeify.entity.Appointment;
import com.hospitalManagement.hazeify.entity.Appointment.AppointmentStatus;
import com.hospitalManagement.hazeify.entity.Doctor;
import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final UserService userService;

    public Appointment bookAppointment(AppointmentDto appointmentDto) {
        // Get doctor
        Doctor doctor = doctorService.getDoctorById(appointmentDto.getDoctorId());

        // Check if doctor is available
        if (!doctor.isAvailable()) {
            throw new RuntimeException("Doctor is not available for appointments");
        }

        // Parse appointment time
        LocalTime appointmentTime = LocalTime.parse(appointmentDto.getTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Check if appointment time is within doctor's visiting hours
        if (appointmentTime.isBefore(doctor.getVisitingStartTime()) ||
                appointmentTime.isAfter(doctor.getVisitingEndTime())) {
            throw new RuntimeException("Appointment time is outside doctor's visiting hours");
        }

        // Check if appointment is in the future
        LocalDateTime appointmentDateTime = appointmentDto.getDate().atTime(appointmentTime);
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book appointments in the past");
        }

        // Check for conflicting appointments
        long conflictingAppointments = appointmentRepository.countConflictingAppointments(
                doctor.getId(), appointmentDto.getDate(), appointmentDateTime);

        if (conflictingAppointments > 0) {
            throw new RuntimeException("This time slot is already booked");
        }

        // Create appointment
        Appointment appointment = new Appointment();
        appointment.setPatientName(appointmentDto.getPatientName());
        appointment.setEmail(appointmentDto.getEmail());
        appointment.setPhone(appointmentDto.getPhone());
        appointment.setDoctor(doctor);
        appointment.setDate(appointmentDto.getDate());
        appointment.setTime(appointmentTime);
        appointment.setNotes(appointmentDto.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);

        // Set patient if user is logged in
        if (appointmentDto.getPatientId() != null) {
            User patient = userService.findById(appointmentDto.getPatientId());
            appointment.setPatient(patient);
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment bookAppointment(Long patientId, AppointmentDto appointmentDto) {
        appointmentDto.setPatientId(patientId);
        return bookAppointment(appointmentDto);
    }

    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Only allow status updates if appointment is in the future or current
        LocalDateTime appointmentDateTime = appointment.getDate().atTime(appointment.getTime());
        if (appointmentDateTime.isBefore(LocalDateTime.now()) &&
                status == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot mark past appointments as completed");
        }

        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findUpcomingAppointmentsByPatient(patientId, LocalDate.now());
    }

    public List<Appointment> getDoctorAppointments(Long doctorId) {
        try {
            return appointmentRepository.findUpcomingAppointmentsByDoctor(doctorId, LocalDate.now());
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Appointment> getDoctorAppointmentsByStatus(Long doctorId, AppointmentStatus status) {
        try {
            return appointmentRepository.findByDoctorIdAndStatus(doctorId, status);
        } catch (Exception e) {
            return List.of();
        }
    }

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllWithRelations();
    }

    // New methods required by controllers
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }
}