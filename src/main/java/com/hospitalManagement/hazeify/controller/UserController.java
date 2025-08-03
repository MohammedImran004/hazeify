package com.hospitalManagement.hazeify.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hospitalManagement.hazeify.dto.LoginDto;
import com.hospitalManagement.hazeify.dto.UserDto;
import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.service.AuthService;
import com.hospitalManagement.hazeify.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userDto") UserDto userDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "signup";
        }

        try {
            User user = authService.register(userDto);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            try {
                User user = userService.findByUsernameOrEmail(authentication.getName());
                model.addAttribute("user", user);

                // Redirect based on user role
                switch (user.getRole()) {
                    case ADMIN:
                        return "redirect:/admin/dashboard";
                    case DOCTOR:
                        return "redirect:/doctor/dashboard";
                    case PATIENT:
                        return "redirect:/patient/dashboard";
                    default:
                        return "redirect:/login";
                }
            } catch (Exception e) {
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpServletResponse response) {
        // Clear JWT cookie
        jakarta.servlet.http.Cookie jwtCookie = new jakarta.servlet.http.Cookie("jwt_token", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete cookie
        response.addCookie(jwtCookie);

        return "redirect:/login";
    }

    // REST API endpoints
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> apiLogin(@Valid @RequestBody LoginDto loginDto) {
        try {
            Map<String, Object> response = authService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> apiRegister(@Valid @RequestBody UserDto userDto) {
        try {
            User user = authService.register(userDto);
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}