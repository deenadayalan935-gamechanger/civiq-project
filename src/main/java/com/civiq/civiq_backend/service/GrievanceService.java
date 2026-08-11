package com.civiq.civiq_backend.service;

import com.civiq.civiq_backend.dto.request.GrievanceRequest;
import com.civiq.civiq_backend.dto.request.StatusUpdateRequest;
import com.civiq.civiq_backend.dto.response.ApiResponse;
import com.civiq.civiq_backend.dto.response.GrievanceResponse;

import java.util.List;
import java.util.UUID;

public interface GrievanceService {

    // Citizen
    GrievanceResponse submitGrievance(GrievanceRequest request, String email);

    List<GrievanceResponse> getMyGrievances(String email);

    GrievanceResponse getGrievanceById(UUID id);

    ApiResponse reopenGrievance(UUID id, String email);

    ApiResponse upvoteGrievance(UUID id, String email);

    ApiResponse removeUpvote(UUID id, String email);

    // Officer
    List<GrievanceResponse> getOfficerGrievances(String email);

    ApiResponse updateStatus(UUID id, StatusUpdateRequest request, String email);

    // Admin
    List<GrievanceResponse> getAllGrievances();

    List<GrievanceResponse> getBreachedSlaGrievances();
}