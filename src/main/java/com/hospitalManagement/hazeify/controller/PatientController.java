package com.hospitalManagement.hazeify.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospitalManagement.hazeify.dto.AppointmentDto;
import com.hospitalManagement.hazeify.entity.Appointment;
import com.hospitalManagement.hazeify.entity.Doctor;
import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.service.AppointmentService;
import com.hospitalManagement.hazeify.service.DoctorService;
import com.hospitalManagement.hazeify.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String patientDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User patient = userService.findByUsernameOrEmail(authentication.getName());

        List<Appointment> appointments = appointmentService.getPatientAppointments(patient.getId());

        long totalAppointments = appointments.size();
        long pendingAppointments = appointments.stream()
                .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.PENDING).count();
        long completedAppointments = appointments.stream()
                .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.COMPLETED).count();

        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointments);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("pendingAppointments", pendingAppointments);
        model.addAttribute("completedAppointments", completedAppointments);

        return "patient/dashboard";
    }

    @GetMapping("/doctors")
    public String viewDoctors(Model model) {
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        model.addAttribute("doctors", doctors);
        return "patient/doctors";
    }

    @GetMapping("/book-appointment")
    public String bookAppointmentGeneral(Model model) {
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("appointmentDto", new AppointmentDto());
        return "patient/book-appointment-general";
    }

    @GetMapping("/book-appointment/{doctorId}")
    public String bookAppointmentForm(@PathVariable Long doctorId, Model model) {
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            model.addAttribute("doctor", doctor);
            model.addAttribute("appointmentDto", new AppointmentDto());
            return "patient/book-appointment";
        } catch (Exception e) {
            return "redirect:/patient/doctors";
        }
    }

    @PostMapping("/book-appointment/{doctorId}")
    public String bookAppointment(@PathVariable Long doctorId,
            @Valid @ModelAttribute("appointmentDto") AppointmentDto appointmentDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form data");
            return "redirect:/patient/book-appointment/" + doctorId;
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User patient = userService.findByUsernameOrEmail(authentication.getName());

            appointmentDto.setDoctorId(doctorId);
            appointmentService.bookAppointment(patient.getId(), appointmentDto);

            redirectAttributes.addFlashAttribute("message", "Appointment booked successfully!");
            return "redirect:/patient/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/book-appointment/" + doctorId;
        }
    }

    @GetMapping("/appointments")
    public String viewMyAppointments(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User patient = userService.findByUsernameOrEmail(authentication.getName());

        List<Appointment> appointments = appointmentService.getPatientAppointments(patient.getId());
        model.addAttribute("appointments", appointments);
        model.addAttribute("patient", patient);

        return "patient/appointments";
    }

    @GetMapping("/appointments/cancel/{appointmentId}")
    public String cancelAppointment(@PathVariable Long appointmentId, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(appointmentId);
            redirectAttributes.addFlashAttribute("message", "Appointment cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/patient/appointments";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User patient = userService.findByUsernameOrEmail(authentication.getName());
        model.addAttribute("patient", patient);
        return "patient/profile";
    }
}