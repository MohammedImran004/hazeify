package com.hospitalManagement.hazeify.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String description;

    @NotNull(message = "Visiting start time is required")
    private LocalTime visitingStartTime;

    @NotNull(message = "Visiting end time is required")
    private LocalTime visitingEndTime;

    private Double consultationFee;

    private boolean isAvailable = true;
}