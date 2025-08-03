package com.hospitalManagement.hazeify.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hospitalManagement.hazeify.dto.LoginDto;
import com.hospitalManagement.hazeify.dto.UserDto;
import com.hospitalManagement.hazeify.entity.User;
import com.hospitalManagement.hazeify.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public Map<String, Object> login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()));

        User user = userService.findByUsername(loginDto.getUsernameOrEmail());
        if (user == null) {
            user = userService.findByEmail(loginDto.getUsernameOrEmail());
        }

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", jwtToken);
        response.put("refresh_token", refreshToken);
        response.put("user", user);

        return response;
    }

    public User register(UserDto userDto) {
        return userService.registerUser(userDto);
    }

    public String refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userService.findByUsername(username);

        if (jwtService.isTokenValid(refreshToken, user)) {
            return jwtService.generateToken(user);
        }

        throw new RuntimeException("Invalid refresh token");
    }
}