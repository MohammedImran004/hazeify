package com.hospitalManagement.hazeify.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospitalManagement.hazeify.entity.Appointment;
import com.hospitalManagement.hazeify.entity.Doctor;
import com.hospitalManagement.hazeify.service.AppointmentService;
import com.hospitalManagement.hazeify.service.DoctorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            Doctor doctor = doctorService.getDoctorByEmail(email);
            List<Appointment> appointments;

            if (date != null) {
                appointments = appointmentService.getAppointmentsByDoctorAndDate(doctor.getId(), date);
            } else {
                appointments = appointmentService.getAppointmentsByDoctor(doctor.getId());
            }

            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}