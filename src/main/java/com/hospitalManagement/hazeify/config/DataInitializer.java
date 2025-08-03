package com.hospitalManagement.hazeify.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hospitalManagement.hazeify.dto.DoctorDto;
import com.hospitalManagement.hazeify.dto.UserDto;
import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.service.DoctorService;
import com.hospitalManagement.hazeify.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final DoctorService doctorService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        createAdminUser();

        // Create sample doctors if they don't exist
        createSampleDoctors();

        // Create sample patients if they don't exist
        createSamplePatients();
    }

    private void createAdminUser() {
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
                log.info("Admin user created successfully: {}", adminUser.getEmail());
                log.info("Admin login credentials:");
                log.info("Username: admin");
                log.info("Email: admin@hazeify.com");
                log.info("Password: admin123");
            } else {
                log.info("Admin user already exists");
            }
        } catch (Exception e) {
            log.error("Error creating admin user: {}", e.getMessage());
        }
    }

    private void createSampleDoctors() {
        try {
            // Create sample doctors if they don't exist
            if (!doctorService.getAllDoctors().isEmpty()) {
                log.info("Sample doctors already exist");
                return;
            }

            // Doctor 1
            DoctorDto doctor1 = new DoctorDto();
            doctor1.setName("Dr. Sarah Johnson");
            doctor1.setSpecialization("Cardiology");
            doctor1.setEmail("sarah.johnson@hazeify.com");
            doctor1.setPhoneNumber("+1234567891");
            doctor1.setDescription("Experienced cardiologist with 15+ years of practice");
            doctor1.setVisitingStartTime(java.time.LocalTime.of(9, 0));
            doctor1.setVisitingEndTime(java.time.LocalTime.of(17, 0));
            doctor1.setConsultationFee(150.0);
            doctor1.setAvailable(true);

            // Doctor 2
            DoctorDto doctor2 = new DoctorDto();
            doctor2.setName("Dr. Michael Chen");
            doctor2.setSpecialization("Neurology");
            doctor2.setEmail("michael.chen@hazeify.com");
            doctor2.setPhoneNumber("+1234567892");
            doctor2.setDescription("Specialist in neurological disorders and brain health");
            doctor2.setVisitingStartTime(java.time.LocalTime.of(8, 30));
            doctor2.setVisitingEndTime(java.time.LocalTime.of(16, 30));
            doctor2.setConsultationFee(200.0);
            doctor2.setAvailable(true);

            // Doctor 3
            DoctorDto doctor3 = new DoctorDto();
            doctor3.setName("Dr. Emily Rodriguez");
            doctor3.setSpecialization("Pediatrics");
            doctor3.setEmail("emily.rodriguez@hazeify.com");
            doctor3.setPhoneNumber("+1234567893");
            doctor3.setDescription("Dedicated pediatrician with expertise in child healthcare");
            doctor3.setVisitingStartTime(java.time.LocalTime.of(10, 0));
            doctor3.setVisitingEndTime(java.time.LocalTime.of(18, 0));
            doctor3.setConsultationFee(120.0);
            doctor3.setAvailable(true);

            doctorService.createDoctor(doctor1);
            doctorService.createDoctor(doctor2);
            doctorService.createDoctor(doctor3);

            log.info("Sample doctors created successfully");
        } catch (Exception e) {
            log.error("Error creating sample doctors: {}", e.getMessage());
        }
    }

    private void createSamplePatients() {
        try {
            // Create sample patients if they don't exist
            if (!userService.getAllUsers().stream().anyMatch(u -> u.getRole() == User.Role.PATIENT)) {
                // Patient 1
                UserDto patient1 = new UserDto();
                patient1.setUsername("john.doe");
                patient1.setEmail("john.doe@example.com");
                patient1.setPassword("patient123");
                patient1.setFullName("John Doe");
                patient1.setPhoneNumber("+1234567894");
                patient1.setRole("PATIENT");

                // Patient 2
                UserDto patient2 = new UserDto();
                patient2.setUsername("jane.smith");
                patient2.setEmail("jane.smith@example.com");
                patient2.setPassword("patient123");
                patient2.setFullName("Jane Smith");
                patient2.setPhoneNumber("+1234567895");
                patient2.setRole("PATIENT");

                userService.registerUser(patient1);
                userService.registerUser(patient2);

                log.info("Sample patients created successfully");
                log.info("Patient login credentials:");
                log.info("Username: john.doe, Password: patient123");
                log.info("Username: jane.smith, Password: patient123");
            } else {
                log.info("Sample patients already exist");
            }
        } catch (Exception e) {
            log.error("Error creating sample patients: {}", e.getMessage());
        }
    }
}