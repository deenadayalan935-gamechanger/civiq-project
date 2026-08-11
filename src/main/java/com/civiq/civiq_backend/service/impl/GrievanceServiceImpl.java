package com.civiq.civiq_backend.service.impl;

import com.civiq.civiq_backend.ai.pipeline.GrievancePipeline;
import com.civiq.civiq_backend.dto.request.GrievanceRequest;
import com.civiq.civiq_backend.dto.request.StatusUpdateRequest;
import com.civiq.civiq_backend.dto.response.ApiResponse;
import com.civiq.civiq_backend.dto.response.GrievanceResponse;
import com.civiq.civiq_backend.entity.*;
import com.civiq.civiq_backend.enums.GrievanceStatus;
import com.civiq.civiq_backend.repository.*;
import com.civiq.civiq_backend.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrievanceServiceImpl implements GrievanceService {

    private final GrievanceRepository grievanceRepository;
    private final UserRepository userRepository;
    private final WardRepository wardRepository;
    private final DepartmentRepository departmentRepository;
    private final OfficerProfileRepository officerProfileRepository;
    private final GrievanceStatusLogRepository statusLogRepository;
    private final GrievanceUpvoteRepository upvoteRepository;
    private final GrievancePipeline grievancePipeline;

    // ─────────────────────────────────────────
    //  CITIZEN METHODS
    // ─────────────────────────────────────────

    @Override
    @Transactional
    public GrievanceResponse submitGrievance(GrievanceRequest request, String email) {

        // Step 1 - Fetch citizen
        User citizen = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        // Step 2 - Fetch department by category
        Department department = departmentRepository
                .findByCategoryCode(request.getCategoryCode())
                .orElseThrow(() -> new RuntimeException("Invalid category code"));

        // Step 3 - Find ward by GPS (simplified - ward detection)
        Ward ward = wardRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No wards found"));

        // Step 4 - Build and save grievance
        Grievance grievance = Grievance.builder()
                .citizen(citizen)
                .title(request.getTitle())
                .description(request.getDescription())
                .categoryCode(request.getCategoryCode())
                .ward(ward)
                .department(department)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .currentStatus(GrievanceStatus.SUBMITTED)
                .upvoteCount(0)
                .build();

        grievanceRepository.save(grievance);

        // Step 5 - Log initial status
        GrievanceStatusLog log = GrievanceStatusLog.builder()
                .grievance(grievance)
                .oldStatus(GrievanceStatus.SUBMITTED)
                .newStatus(GrievanceStatus.SUBMITTED)
                .changedBy(citizen)
                .remarks("Grievance submitted by citizen")
                .build();

        statusLogRepository.save(log);

        statusLogRepository.save(log);

// Trigger AI pipeline asynchronously
        grievancePipeline.process(grievance);

        

        return mapToResponse(grievance);
    }

    @Override
    public List<GrievanceResponse> getMyGrievances(String email) {
        User citizen = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        return grievanceRepository.findByCitizenId(citizen.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GrievanceResponse getGrievanceById(UUID id) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));
        return mapToResponse(grievance);
    }

    @Override
    @Transactional
    public ApiResponse reopenGrievance(UUID id, String email) {
        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        User citizen = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        grievance.setCurrentStatus(GrievanceStatus.REOPENED);
        grievanceRepository.save(grievance);

        GrievanceStatusLog log = GrievanceStatusLog.builder()
                .grievance(grievance)
                .oldStatus(GrievanceStatus.RESOLVED)
                .newStatus(GrievanceStatus.REOPENED)
                .changedBy(citizen)
                .remarks("Citizen reopened the grievance")
                .build();

        statusLogRepository.save(log);

        return new ApiResponse(true, "Grievance reopened successfully");
    }

    @Override
    @Transactional
    public ApiResponse upvoteGrievance(UUID id, String email) {
        User citizen = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        // Check if already upvoted
        if (upvoteRepository.existsByCitizenIdAndGrievanceId(
                citizen.getId(), grievance.getId())) {
            return new ApiResponse(false, "Already upvoted");
        }

        // Save upvote record
        GrievanceUpvote upvote = GrievanceUpvote.builder()
                .citizen(citizen)
                .grievance(grievance)
                .build();
        upvoteRepository.save(upvote);

        // Increment counter cache
        grievance.setUpvoteCount(grievance.getUpvoteCount() + 1);
        grievanceRepository.save(grievance);

        return new ApiResponse(true, "Upvoted successfully");
    }

    @Override
    @Transactional
    public ApiResponse removeUpvote(UUID id, String email) {
        User citizen = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        GrievanceUpvote upvote = upvoteRepository
                .findByCitizenIdAndGrievanceId(citizen.getId(), grievance.getId())
                .orElseThrow(() -> new RuntimeException("Upvote not found"));

        upvoteRepository.delete(upvote);

        // Decrement counter cache
        grievance.setUpvoteCount(Math.max(0, grievance.getUpvoteCount() - 1));
        grievanceRepository.save(grievance);

        return new ApiResponse(true, "Upvote removed successfully");
    }

    // ─────────────────────────────────────────
    //  OFFICER METHODS
    // ─────────────────────────────────────────

    @Override
    public List<GrievanceResponse> getOfficerGrievances(String email) {
        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        OfficerProfile profile = officerProfileRepository
                .findByUserId(officer.getId())
                .orElseThrow(() -> new RuntimeException("Officer profile not found"));

        return grievanceRepository.findByWardIdAndDepartmentId(
                        profile.getWard().getId(),
                        profile.getDepartment().getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiResponse updateStatus(UUID id, StatusUpdateRequest request, String email) {
        User officer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        Grievance grievance = grievanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        GrievanceStatus oldStatus = grievance.getCurrentStatus();

        grievance.setCurrentStatus(request.getNewStatus());
        grievanceRepository.save(grievance);

        GrievanceStatusLog log = GrievanceStatusLog.builder()
                .grievance(grievance)
                .oldStatus(oldStatus)
                .newStatus(request.getNewStatus())
                .changedBy(officer)
                .remarks(request.getRemarks())
                .build();

        statusLogRepository.save(log);

        return new ApiResponse(true, "Status updated successfully");
    }

    // ─────────────────────────────────────────
    //  ADMIN METHODS
    // ─────────────────────────────────────────

    @Override
    public List<GrievanceResponse> getAllGrievances() {
        return grievanceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GrievanceResponse> getBreachedSlaGrievances() {
        return grievanceRepository.findBreachedSlaGrievances()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    //  MAPPER
    // ─────────────────────────────────────────

    private GrievanceResponse mapToResponse(Grievance grievance) {
        GrievanceResponse response = new GrievanceResponse();
        response.setId(grievance.getId());
        response.setTitle(grievance.getTitle());
        response.setDescription(grievance.getDescription());
        response.setCategoryCode(grievance.getCategoryCode());
        response.setWardName(grievance.getWard().getName());
        response.setDepartmentName(grievance.getDepartment().getName());
        response.setLatitude(grievance.getLatitude());
        response.setLongitude(grievance.getLongitude());
        response.setCurrentStatus(grievance.getCurrentStatus());
        response.setUpvoteCount(grievance.getUpvoteCount());
        response.setSlaDeadline(grievance.getSlaDeadline());
        response.setCreatedAt(grievance.getCreatedAt());
        response.setUpdatedAt(grievance.getUpdatedAt());

        if (grievance.getAssignedOfficer() != null) {
            response.setAssignedOfficerName(
                    grievance.getAssignedOfficer().getFullName());
        }

        return response;
    }
}