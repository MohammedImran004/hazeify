package com.hospitalManagement.hazeify.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppointmentViewController {

    private final UserService userService;

    @GetMapping("/appointments/book")
    public String appointmentForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            try {
                User user = userService.findByUsernameOrEmail(authentication.getName());

                // Redirect based on user role
                switch (user.getRole()) {
                    case PATIENT:
                        return "redirect:/patient/book-appointment";
                    case DOCTOR:
                        return "redirect:/doctor/appointments";
                    case ADMIN:
                        return "redirect:/admin/dashboard";
                    default:
                        return "redirect:/login";
                }
            } catch (Exception e) {
                return "redirect:/login";
            }
        }

        // If not authenticated, redirect to login
        return "redirect:/login";
    }

    @GetMapping("/book-appointment")
    public String generalBookAppointment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            try {
                User user = userService.findByUsernameOrEmail(authentication.getName());

                // Redirect based on user role
                switch (user.getRole()) {
                    case PATIENT:
                        return "redirect:/patient/book-appointment";
                    case DOCTOR:
                        return "redirect:/doctor/appointments";
                    case ADMIN:
                        return "redirect:/admin/dashboard";
                    default:
                        return "redirect:/login";
                }
            } catch (Exception e) {
                return "redirect:/login";
            }
        }

        // If not authenticated, redirect to login
        return "redirect:/login";
    }
}