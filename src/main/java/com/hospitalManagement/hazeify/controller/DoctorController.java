package com.hospitalManagement.hazeify.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospitalManagement.hazeify.entity.Appointment;
import com.hospitalManagement.hazeify.entity.Appointment.AppointmentStatus;
import com.hospitalManagement.hazeify.entity.Doctor;
import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.service.AppointmentService;
import com.hospitalManagement.hazeify.service.DoctorService;
import com.hospitalManagement.hazeify.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final DoctorService doctorService;

    @GetMapping("/health")
    public String healthCheck() {
        return "Doctor controller is working!";
    }

    @GetMapping("/")
    public String doctorHome() {
        return "redirect:/doctor/dashboard";
    }

    @GetMapping("/not-registered")
    public String notRegistered(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        try {
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());
            if (doctorUser == null) {
                return "redirect:/login";
            }

            model.addAttribute("error", "Your email is not registered by the admin");
            model.addAttribute("userEmail", doctorUser.getEmail());
            return "doctor/not-registered";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard")
    public String doctorDashboard(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            // Find doctor by email (assuming doctor email matches user email)
            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                model.addAttribute("error", "Your email is not registered by the admin");
                model.addAttribute("userEmail", doctorUser.getEmail());
                return "doctor/not-registered";
            }

            List<Appointment> allAppointments = appointmentService.getDoctorAppointments(doctor.getId());
            List<Appointment> pendingAppointments = appointmentService.getDoctorAppointmentsByStatus(doctor.getId(),
                    AppointmentStatus.PENDING);
            List<Appointment> confirmedAppointments = appointmentService.getDoctorAppointmentsByStatus(doctor.getId(),
                    AppointmentStatus.CONFIRMED);
            List<Appointment> completedAppointments = appointmentService.getDoctorAppointmentsByStatus(doctor.getId(),
                    AppointmentStatus.COMPLETED);

            // Handle null lists gracefully
            allAppointments = allAppointments != null ? allAppointments : List.of();
            pendingAppointments = pendingAppointments != null ? pendingAppointments : List.of();
            confirmedAppointments = confirmedAppointments != null ? confirmedAppointments : List.of();
            completedAppointments = completedAppointments != null ? completedAppointments : List.of();

            long totalAppointments = allAppointments.size();
            long pendingCount = pendingAppointments.size();
            long confirmedCount = confirmedAppointments.size();
            long completedCount = completedAppointments.size();

            model.addAttribute("doctor", doctor);
            model.addAttribute("totalAppointments", totalAppointments);
            model.addAttribute("pendingAppointments", pendingAppointments);
            model.addAttribute("confirmedAppointments", confirmedAppointments);
            model.addAttribute("completedAppointments", completedAppointments);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("confirmedCount", confirmedCount);
            model.addAttribute("completedCount", completedCount);

            return "doctor/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                model.addAttribute("error", "Your email is not registered by the admin");
                model.addAttribute("userEmail", doctorUser.getEmail());
                return "doctor/not-registered";
            }

            List<Appointment> appointments = appointmentService.getDoctorAppointments(doctor.getId());
            appointments = appointments != null ? appointments : List.of();
            model.addAttribute("appointments", appointments);
            model.addAttribute("doctor", doctor);

            return "doctor/appointments";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading appointments: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/appointments/pending")
    public String viewPendingAppointments(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                model.addAttribute("error", "Your email is not registered by the admin");
                model.addAttribute("userEmail", doctorUser.getEmail());
                return "doctor/not-registered";
            }

            List<Appointment> appointments = appointmentService.getDoctorAppointmentsByStatus(doctor.getId(),
                    AppointmentStatus.PENDING);
            appointments = appointments != null ? appointments : List.of();
            model.addAttribute("appointments", appointments);
            model.addAttribute("doctor", doctor);
            model.addAttribute("status", "Pending");

            return "doctor/appointments";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading pending appointments: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/appointments/confirmed")
    public String viewConfirmedAppointments(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                model.addAttribute("error", "Your email is not registered by the admin");
                model.addAttribute("userEmail", doctorUser.getEmail());
                return "doctor/not-registered";
            }

            List<Appointment> appointments = appointmentService.getDoctorAppointmentsByStatus(doctor.getId(),
                    AppointmentStatus.CONFIRMED);
            appointments = appointments != null ? appointments : List.of();
            model.addAttribute("appointments", appointments);
            model.addAttribute("doctor", doctor);
            model.addAttribute("status", "Confirmed");

            return "doctor/appointments";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading confirmed appointments: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/appointments/completed")
    public String viewCompletedAppointments(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                model.addAttribute("error", "Your email is not registered by the admin");
                model.addAttribute("userEmail", doctorUser.getEmail());
                return "doctor/not-registered";
            }

            List<Appointment> appointments = appointmentService.getDoctorAppointmentsByStatus(doctor.getId(),
                    AppointmentStatus.COMPLETED);
            appointments = appointments != null ? appointments : List.of();
            model.addAttribute("appointments", appointments);
            model.addAttribute("doctor", doctor);
            model.addAttribute("status", "Completed");

            return "doctor/appointments";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading completed appointments: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/appointments/{appointmentId}/confirm")
    public String confirmAppointment(@PathVariable Long appointmentId, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateAppointmentStatus(appointmentId, AppointmentStatus.CONFIRMED);
            redirectAttributes.addFlashAttribute("message", "Appointment confirmed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/doctor/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/complete")
    public String completeAppointment(@PathVariable Long appointmentId, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateAppointmentStatus(appointmentId, AppointmentStatus.COMPLETED);
            redirectAttributes.addFlashAttribute("message", "Appointment marked as completed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/doctor/appointments";
    }

    @PostMapping("/appointments/{appointmentId}/status")
    public String updateAppointmentStatus(@PathVariable Long appointmentId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            AppointmentStatus newStatus = AppointmentStatus.valueOf(status.toUpperCase());
            appointmentService.updateAppointmentStatus(appointmentId, newStatus);
            redirectAttributes.addFlashAttribute("message", "Appointment status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/doctor/appointments";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                model.addAttribute("error", "Your email is not registered by the admin");
                model.addAttribute("userEmail", doctorUser.getEmail());
                return "doctor/not-registered";
            }

            model.addAttribute("doctor", doctor);
            model.addAttribute("user", doctorUser);

            return "doctor/edit-profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("specialization") String specialization,
            @RequestParam("consultationFee") Double consultationFee,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/doctor/profile";
            }

            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                redirectAttributes.addFlashAttribute("error", "Doctor not found");
                return "redirect:/doctor/profile";
            }

            // Update doctor information
            doctor.setName(name);
            doctor.setEmail(email);
            doctor.setPhoneNumber(phoneNumber);
            doctor.setSpecialization(specialization);
            doctor.setConsultationFee(consultationFee);
            doctor.setDescription(description);

            doctorService.updateDoctor(doctor);

            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
            return "redirect:/doctor/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/doctor/profile/edit";
        }
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User doctorUser = userService.findByUsernameOrEmail(authentication.getName());

            if (doctorUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            Doctor doctor = doctorService.getDoctorByEmail(doctorUser.getEmail());

            if (doctor == null) {
                model.addAttribute("error", "Your email is not registered by the admin");
                model.addAttribute("userEmail", doctorUser.getEmail());
                return "doctor/not-registered";
            }

            model.addAttribute("doctor", doctor);
            model.addAttribute("user", doctorUser);

            return "doctor/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }
}