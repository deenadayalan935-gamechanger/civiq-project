package com.civiq.civiq_backend.service.impl;

import com.civiq.civiq_backend.dto.request.LoginRequest;
import com.civiq.civiq_backend.dto.request.RegisterRequest;
import com.civiq.civiq_backend.dto.response.ApiResponse;
import com.civiq.civiq_backend.dto.response.JwtResponse;
import com.civiq.civiq_backend.entity.User;
import com.civiq.civiq_backend.enums.Role;
import com.civiq.civiq_backend.repository.UserRepository;
import com.civiq.civiq_backend.security.JwtUtil;
import com.civiq.civiq_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public ApiResponse register(RegisterRequest request) {

        // Step 1 - Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email is already registered");
        }

        // Step 2 - Check if phone already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return new ApiResponse(false, "Phone number is already registered");
        }

        // Step 3 - Build and save the new user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.CITIZEN)
                .isVerified(false)
                .isActive(true)
                .build();

        userRepository.save(user);

        return new ApiResponse(true, "Registration successful");
    }

    @Override
    public JwtResponse login(LoginRequest request) {

        // Step 1 - Authenticate email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2 - Fetch user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 3 - Generate JWT token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // Step 4 - Return token + user info
        return new JwtResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }
}