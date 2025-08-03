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

        // Parse appointment time - handle both HH:mm and HH:mm:ss formats
        LocalTime appointmentTime;
        try {
            appointmentTime = LocalTime.parse(appointmentDto.getTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e) {
            try {
                appointmentTime = LocalTime.parse(appointmentDto.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e2) {
                throw new RuntimeException("Invalid time format. Please use HH:mm or HH:mm:ss format");
            }
        }

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
                doctor.getId(), appointmentDto.getDate(), appointmentTime);

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

    /**
     * Get next available time slots for a doctor on a specific date
     * Ensures 10-minute gap between appointments
     */
    public List<LocalTime> getNextAvailableTimeSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        List<Appointment> existingAppointments = getAppointmentsByDoctorAndDate(doctorId, date);

        List<LocalTime> availableSlots = new java.util.ArrayList<>();
        LocalTime startTime = doctor.getVisitingStartTime();
        LocalTime endTime = doctor.getVisitingEndTime();

        // Generate 30-minute slots with 10-minute gaps
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            LocalTime slotEndTime = currentTime.plusMinutes(30);

            // Check if this slot conflicts with existing appointments
            boolean isAvailable = true;
            for (Appointment appointment : existingAppointments) {
                if (appointment.getStatus() == AppointmentStatus.PENDING ||
                        appointment.getStatus() == AppointmentStatus.CONFIRMED) {

                    LocalTime appointmentStart = appointment.getTime();
                    LocalTime appointmentEnd = appointmentStart.plusMinutes(30);

                    // Check for overlap (including 10-minute gap)
                    if (!(slotEndTime.plusMinutes(10).isBefore(appointmentStart) ||
                            currentTime.isAfter(appointmentEnd.plusMinutes(10)))) {
                        isAvailable = false;
                        break;
                    }
                }
            }

            if (isAvailable) {
                availableSlots.add(currentTime);
            }

            currentTime = currentTime.plusMinutes(40); // 30 min slot + 10 min gap
        }

        return availableSlots;
    }

    /**
     * Update appointment status with validation
     */
    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = getAppointmentById(appointmentId);

        // Validate status transition
        if (!isValidStatusTransition(appointment.getStatus(), newStatus)) {
            throw new RuntimeException(
                    "Invalid status transition from " + appointment.getStatus() + " to " + newStatus);
        }

        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

    /**
     * Validate status transitions
     */
    private boolean isValidStatusTransition(AppointmentStatus currentStatus, AppointmentStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return newStatus == AppointmentStatus.CONFIRMED ||
                        newStatus == AppointmentStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == AppointmentStatus.COMPLETED ||
                        newStatus == AppointmentStatus.CANCELLED;
            case COMPLETED:
                return false; // Cannot change completed appointments
            case CANCELLED:
                return false; // Cannot change cancelled appointments
            default:
                return false;
        }
    }
}