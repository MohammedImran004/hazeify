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
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is logged in, redirect to appropriate dashboard
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            try {
                User user = userService.findByUsernameOrEmail(authentication.getName());

                if (user == null) {
                    return "index"; // Show home page if user not found
                }

                // Redirect based on user role
                switch (user.getRole()) {
                    case ADMIN:
                        return "redirect:/admin/dashboard";
                    case DOCTOR:
                        return "redirect:/doctor/dashboard";
                    case PATIENT:
                        return "redirect:/patient/dashboard";
                    default:
                        return "index"; // Show home page for unknown roles
                }
            } catch (Exception e) {
                return "index"; // Show home page if user lookup fails
            }
        }

        // Show home page for non-authenticated users
        return "index";
    }
}