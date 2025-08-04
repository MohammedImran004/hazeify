package com.hospitalManagement.hazeify.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDto {
    private Long id;
    private String name;
    private String specialization;
    private String email;
    private String phoneNumber;
    private String description;
    private LocalTime visitingStartTime;
    private LocalTime visitingEndTime;
    private Double consultationFee;
    private boolean available;
}