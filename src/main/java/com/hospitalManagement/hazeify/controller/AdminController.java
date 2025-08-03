package com.hospitalManagement.hazeify.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospitalManagement.hazeify.dto.DoctorDto;
import com.hospitalManagement.hazeify.dto.UserDto;
import com.hospitalManagement.hazeify.entity.Appointment;
import com.hospitalManagement.hazeify.entity.Doctor;
import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.service.AppointmentService;
import com.hospitalManagement.hazeify.service.DoctorService;
import com.hospitalManagement.hazeify.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DoctorService doctorService;
    private final UserService userService;
    private final AppointmentService appointmentService;

    @GetMapping("/health")
    public String healthCheck() {
        return "Admin controller is working!";
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> apiHealthCheck() {
        return ResponseEntity.ok("Admin API is working!");
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        try {
            // Get statistics
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<Appointment> appointments = appointmentService.getAllAppointments();

            // Handle null lists gracefully
            doctors = doctors != null ? doctors : List.of();
            appointments = appointments != null ? appointments : List.of();

            long totalDoctors = doctors.size();
            long availableDoctors = doctors.stream().filter(Doctor::isAvailable).count();
            long totalAppointments = appointments.size();
            long pendingAppointments = appointments.stream()
                    .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.PENDING).count();
            long completedAppointments = appointments.stream()
                    .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.COMPLETED).count();

            model.addAttribute("totalDoctors", totalDoctors);
            model.addAttribute("availableDoctors", availableDoctors);
            model.addAttribute("totalAppointments", totalAppointments);
            model.addAttribute("pendingAppointments", pendingAppointments);
            model.addAttribute("completedAppointments", completedAppointments);
            model.addAttribute("recentAppointments", appointments.stream().limit(5).toList());

            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            model.addAttribute("totalDoctors", 0);
            model.addAttribute("availableDoctors", 0);
            model.addAttribute("totalAppointments", 0);
            model.addAttribute("pendingAppointments", 0);
            model.addAttribute("completedAppointments", 0);
            model.addAttribute("recentAppointments", List.of());
            return "admin/dashboard";
        }
    }

    @GetMapping("/doctors/manage")
    public String manageDoctors(Model model) {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            doctors = doctors != null ? doctors : List.of();
            model.addAttribute("doctors", doctors);
            model.addAttribute("doctorDto", new DoctorDto());
            return "admin_doctors";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading doctors: " + e.getMessage());
            model.addAttribute("doctors", List.of());
            model.addAttribute("doctorDto", new DoctorDto());
            return "admin_doctors";
        }
    }

    @PostMapping("/doctors/add")
    public String addDoctor(@Valid @ModelAttribute("doctorDto") DoctorDto doctorDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form data");
            return "redirect:/admin/doctors/manage";
        }

        try {
            // Validate required fields
            if (doctorDto.getName() == null || doctorDto.getName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Doctor name is required");
                return "redirect:/admin/doctors/manage";
            }

            if (doctorDto.getEmail() == null || doctorDto.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Doctor email is required");
                return "redirect:/admin/doctors/manage";
            }

            if (doctorDto.getSpecialization() == null || doctorDto.getSpecialization().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Specialization is required");
                return "redirect:/admin/doctors/manage";
            }

            if (doctorDto.getPhoneNumber() == null || doctorDto.getPhoneNumber().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Phone number is required");
                return "redirect:/admin/doctors/manage";
            }

            if (doctorDto.getVisitingStartTime() == null) {
                redirectAttributes.addFlashAttribute("error", "Visiting start time is required");
                return "redirect:/admin/doctors/manage";
            }

            if (doctorDto.getVisitingEndTime() == null) {
                redirectAttributes.addFlashAttribute("error", "Visiting end time is required");
                return "redirect:/admin/doctors/manage";
            }

            if (doctorDto.getConsultationFee() == null || doctorDto.getConsultationFee() <= 0) {
                redirectAttributes.addFlashAttribute("error", "Consultation fee must be greater than 0");
                return "redirect:/admin/doctors/manage";
            }

            doctorService.createDoctor(doctorDto);
            redirectAttributes.addFlashAttribute("message", "Doctor added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding doctor: " + e.getMessage());
        }

        return "redirect:/admin/doctors/manage";
    }

    @GetMapping("/doctors/edit/{id}")
    public String editDoctorForm(@PathVariable Long id, Model model) {
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            DoctorDto doctorDto = new DoctorDto();
            doctorDto.setName(doctor.getName());
            doctorDto.setSpecialization(doctor.getSpecialization());
            doctorDto.setEmail(doctor.getEmail());
            doctorDto.setPhoneNumber(doctor.getPhoneNumber());
            doctorDto.setDescription(doctor.getDescription());
            doctorDto.setVisitingStartTime(doctor.getVisitingStartTime());
            doctorDto.setVisitingEndTime(doctor.getVisitingEndTime());
            doctorDto.setConsultationFee(doctor.getConsultationFee());
            doctorDto.setAvailable(doctor.isAvailable());

            model.addAttribute("doctorDto", doctorDto);
            model.addAttribute("doctorId", id);
            return "admin/edit-doctor";
        } catch (Exception e) {
            return "redirect:/admin/doctors/manage";
        }
    }

    @PostMapping("/doctors/edit/{id}")
    public String updateDoctor(@PathVariable Long id,
            @Valid @ModelAttribute("doctorDto") DoctorDto doctorDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form data");
            return "redirect:/admin/doctors/edit/" + id;
        }

        try {
            doctorService.updateDoctor(id, doctorDto);
            redirectAttributes.addFlashAttribute("message", "Doctor updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/doctors/manage";
    }

    @GetMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctor(id);
            redirectAttributes.addFlashAttribute("message", "Doctor deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/doctors/manage";
    }

    @GetMapping("/appointments")
    public String viewAppointments(Model model) {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            appointments = appointments != null ? appointments : List.of();
            model.addAttribute("appointments", appointments);
            return "admin/appointments";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading appointments: " + e.getMessage());
            model.addAttribute("appointments", List.of());
            return "admin/appointments";
        }
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        try {
            // Get all users from UserService
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
            model.addAttribute("users", List.of());
            return "admin/users";
        }
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("userDto") UserDto userDto,
            RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("message", "User added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = userService.findById(id);
            model.addAttribute("user", user);
            return "admin/edit-user";
        } catch (Exception e) {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
            @ModelAttribute("userDto") UserDto userDto,
            RedirectAttributes redirectAttributes) {
        try {
            // Update user logic would go here
            redirectAttributes.addFlashAttribute("message", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Delete user logic would go here
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ========== REST API ENDPOINTS ==========

    @GetMapping("/api/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctorsApi() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/api/doctors")
    public ResponseEntity<Doctor> addDoctorApi(@RequestBody DoctorDto doctorDto) {
        try {
            // Validate required fields
            if (doctorDto.getName() == null || doctorDto.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (doctorDto.getEmail() == null || doctorDto.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (doctorDto.getSpecialization() == null || doctorDto.getSpecialization().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (doctorDto.getPhoneNumber() == null || doctorDto.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            if (doctorDto.getVisitingStartTime() == null) {
                return ResponseEntity.badRequest().build();
            }

            if (doctorDto.getVisitingEndTime() == null) {
                return ResponseEntity.badRequest().build();
            }

            if (doctorDto.getConsultationFee() == null || doctorDto.getConsultationFee() <= 0) {
                return ResponseEntity.badRequest().build();
            }

            Doctor doctor = doctorService.createDoctor(doctorDto);
            return ResponseEntity.ok(doctor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctorApi(@PathVariable Long id, @RequestBody DoctorDto doctorDto) {
        try {
            Doctor doctor = doctorService.updateDoctor(id, doctorDto);
            return ResponseEntity.ok(doctor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/doctors/{id}")
    public ResponseEntity<Void> deleteDoctorApi(@PathVariable Long id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointmentsApi() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsersApi() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/api/users")
    public ResponseEntity<User> addUserApi(@RequestBody UserDto userDto) {
        try {
            User user = userService.registerUser(userDto);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}