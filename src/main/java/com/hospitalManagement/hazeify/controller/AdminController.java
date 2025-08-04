package com.hospitalManagement.hazeify.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospitalManagement.hazeify.dto.DoctorDto;
import com.hospitalManagement.hazeify.dto.DoctorResponseDto;
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
        System.out.println("DEBUG: /admin/health endpoint accessed");
        return "Admin controller is working!";
    }

    @GetMapping("/test")
    public String testEndpoint() {
        System.out.println("DEBUG: /admin/test endpoint accessed");
        return "Admin test endpoint is working!";
    }

    @GetMapping("/test-page")
    public String testPage() {
        System.out.println("DEBUG: /admin/test-page endpoint accessed");
        return "admin/test";
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> apiHealthCheck() {
        return ResponseEntity.ok("Admin API is working!");
    }

    @GetMapping("/setup")
    public String setupAdmin(Model model) {
        try {
            // Check if admin user already exists
            if (!userService.existsByEmail("admin@hazeify.com")) {
                UserDto adminDto = new UserDto();
                adminDto.setUsername("admin");
                adminDto.setEmail("admin@hazeify.com");
                adminDto.setPassword("admin123");
                adminDto.setFullName("System Administrator");
                adminDto.setPhoneNumber("+1234567890");
                adminDto.setRole("ADMIN");

                User adminUser = userService.registerUser(adminDto);
                model.addAttribute("message", "Admin user created successfully!");
                model.addAttribute("username", "admin");
                model.addAttribute("email", "admin@hazeify.com");
                model.addAttribute("password", "admin123");
            } else {
                model.addAttribute("message", "Admin user already exists!");
                model.addAttribute("username", "admin");
                model.addAttribute("email", "admin@hazeify.com");
                model.addAttribute("password", "admin123");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error creating admin user: " + e.getMessage());
        }
        return "admin/setup";
    }

    @GetMapping("/create-admin")
    public ResponseEntity<String> createAdminApi() {
        try {
            // Check if admin user already exists
            if (!userService.existsByEmail("admin@hazeify.com")) {
                UserDto adminDto = new UserDto();
                adminDto.setUsername("admin");
                adminDto.setEmail("admin@hazeify.com");
                adminDto.setPassword("admin123");
                adminDto.setFullName("System Administrator");
                adminDto.setPhoneNumber("+1234567890");
                adminDto.setRole("ADMIN");

                User adminUser = userService.registerUser(adminDto);
                return ResponseEntity.ok("Admin user created successfully!");
            } else {
                return ResponseEntity.ok("Admin user already exists!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating admin user: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            List<User> users = userService.getAllUsers();
            List<Appointment> appointments = appointmentService.getAllAppointments();

            long totalDoctors = doctors.size();
            long totalUsers = users.size();
            long totalAppointments = appointments.size();

            // Count appointments by status
            long pendingAppointments = appointments.stream()
                    .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.PENDING).count();
            long confirmedAppointments = appointments.stream()
                    .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.CONFIRMED).count();
            long completedAppointments = appointments.stream()
                    .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.COMPLETED).count();

            model.addAttribute("totalDoctors", totalDoctors);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalAppointments", totalAppointments);
            model.addAttribute("pendingAppointments", pendingAppointments);
            model.addAttribute("confirmedAppointments", confirmedAppointments);
            model.addAttribute("completedAppointments", completedAppointments);

            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/doctors")
    public String doctorsRedirect() {
        return "redirect:/admin/doctors/manage";
    }

    @GetMapping("/doctors/manage")
    public String manageDoctors(Model model) {
        try {
            // Don't load doctors here since the template uses JavaScript to fetch from API
            model.addAttribute("doctorDto", new DoctorDto());
            return "admin/doctors";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading doctors: " + e.getMessage());
            return "error";
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
            doctorService.createDoctor(doctorDto);
            redirectAttributes.addFlashAttribute("message", "Doctor added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/doctors/manage";
    }

    @GetMapping("/doctors/edit/{id}")
    public String editDoctorForm(@PathVariable Long id, Model model) {
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            DoctorDto doctorDto = new DoctorDto();
            doctorDto.setName(doctor.getName());
            doctorDto.setEmail(doctor.getEmail());
            doctorDto.setPhoneNumber(doctor.getPhoneNumber());
            doctorDto.setSpecialization(doctor.getSpecialization());
            doctorDto.setDescription(doctor.getDescription());
            doctorDto.setVisitingStartTime(doctor.getVisitingStartTime());
            doctorDto.setVisitingEndTime(doctor.getVisitingEndTime());
            doctorDto.setConsultationFee(doctor.getConsultationFee());
            doctorDto.setAvailable(doctor.isAvailable());

            model.addAttribute("doctorDto", doctorDto);
            model.addAttribute("doctorId", id);
            return "admin/edit-doctor";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading doctor: " + e.getMessage());
            return "error";
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
    public String viewAppointments(Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status) {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();

            // Filter by date if provided
            if (date != null) {
                appointments = appointments.stream()
                        .filter(apt -> apt.getDate().equals(date))
                        .collect(Collectors.toList());
            }

            // Filter by status if provided
            if (status != null && !status.isEmpty()) {
                Appointment.AppointmentStatus statusEnum = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
                appointments = appointments.stream()
                        .filter(apt -> apt.getStatus() == statusEnum)
                        .collect(Collectors.toList());
            }

            model.addAttribute("appointments", appointments);
            model.addAttribute("selectedDate", date);
            model.addAttribute("selectedStatus", status);
            return "admin/appointments";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading appointments: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("userDto", new UserDto());
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
            return "error";
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
            model.addAttribute("error", "Error loading user: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
            @ModelAttribute("userDto") UserDto userDto,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            user.setFullName(userDto.getFullName());
            user.setEmail(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("message", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // REST API endpoints
    @GetMapping("/api/doctors")
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctorsApi() {
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            System.out.println("API: Found " + doctors.size() + " doctors");

            List<DoctorResponseDto> doctorDtos = doctors.stream()
                    .map(doctor -> new DoctorResponseDto(
                            doctor.getId(),
                            doctor.getName(),
                            doctor.getSpecialization(),
                            doctor.getEmail(),
                            doctor.getPhoneNumber(),
                            doctor.getDescription(),
                            doctor.getVisitingStartTime(),
                            doctor.getVisitingEndTime(),
                            doctor.getConsultationFee(),
                            doctor.isAvailable()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(doctorDtos);
        } catch (Exception e) {
            System.err.println("API Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/api/doctors")
    public ResponseEntity<Doctor> addDoctorApi(@RequestBody DoctorDto doctorDto) {
        try {
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

    // Public endpoint for getting available doctors (no admin authentication
    // required)
    @GetMapping("/api/public/doctors")
    public ResponseEntity<List<DoctorResponseDto>> getAvailableDoctorsPublic() {
        try {
            List<Doctor> doctors = doctorService.getAvailableDoctors();

            List<DoctorResponseDto> doctorDtos = doctors.stream()
                    .map(doctor -> new DoctorResponseDto(
                            doctor.getId(),
                            doctor.getName(),
                            doctor.getSpecialization(),
                            doctor.getEmail(),
                            doctor.getPhoneNumber(),
                            doctor.getDescription(),
                            doctor.getVisitingStartTime(),
                            doctor.getVisitingEndTime(),
                            doctor.getConsultationFee(),
                            doctor.isAvailable()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(doctorDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Debug endpoint to test doctor data
    @GetMapping("/api/debug/doctors")
    public ResponseEntity<?> debugDoctors() {
        try {
            List<Doctor> allDoctors = doctorService.getAllDoctors();
            List<Doctor> availableDoctors = doctorService.getAvailableDoctors();

            return ResponseEntity.ok(Map.of(
                    "totalDoctors", allDoctors.size(),
                    "availableDoctors", availableDoctors.size(),
                    "allDoctors", allDoctors,
                    "availableDoctorsList", availableDoctors));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/appointments/{appointmentId}/status")
    public String updateAppointmentStatus(@PathVariable Long appointmentId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes) {
        try {
            Appointment.AppointmentStatus newStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            appointmentService.updateAppointmentStatus(appointmentId, newStatus);
            redirectAttributes.addFlashAttribute("message", "Appointment status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/appointments";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User adminUser = userService.findByUsernameOrEmail(authentication.getName());

            if (adminUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            model.addAttribute("user", adminUser);
            return "admin/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User adminUser = userService.findByUsernameOrEmail(authentication.getName());

            if (adminUser == null) {
                model.addAttribute("error", "User not found");
                return "error";
            }

            model.addAttribute("user", adminUser);
            return "admin/edit-profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User adminUser = userService.findByUsernameOrEmail(authentication.getName());

            if (adminUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/admin/profile";
            }

            // Update admin information
            adminUser.setFullName(fullName);
            adminUser.setPhoneNumber(phoneNumber);

            // Only update email if it's different and not already taken
            if (!adminUser.getEmail().equals(email)) {
                if (userService.existsByEmail(email)) {
                    redirectAttributes.addFlashAttribute("error", "Email already exists");
                    return "redirect:/admin/profile/edit";
                }
                adminUser.setEmail(email);
            }

            userService.updateUser(adminUser);

            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
            return "redirect:/admin/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/profile/edit";
        }
    }
}