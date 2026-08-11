package com.civiq.civiq_backend.service;

import com.civiq.civiq_backend.dto.request.LoginRequest;
import com.civiq.civiq_backend.dto.request.RegisterRequest;
import com.civiq.civiq_backend.dto.response.ApiResponse;
import com.civiq.civiq_backend.dto.response.JwtResponse;

public interface AuthService {

    ApiResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);
}