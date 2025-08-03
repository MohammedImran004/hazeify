package com.hospitalManagement.hazeify.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long patientId; // Optional, for logged-in users

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotNull(message = "Appointment date is required")
    private LocalDate date;

    @NotBlank(message = "Appointment time is required")
    private String time; // Format: "HH:mm:ss"

    private String notes;
}