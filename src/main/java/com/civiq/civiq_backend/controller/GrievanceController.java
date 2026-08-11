package com.civiq.civiq_backend.controller;

import com.civiq.civiq_backend.dto.request.GrievanceRequest;
import com.civiq.civiq_backend.dto.request.StatusUpdateRequest;
import com.civiq.civiq_backend.dto.response.ApiResponse;
import com.civiq.civiq_backend.dto.response.GrievanceResponse;
import com.civiq.civiq_backend.service.GrievanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GrievanceController {

    private final GrievanceService grievanceService;

    // ─────────────────────────────────────────
    //  CITIZEN ENDPOINTS
    // ─────────────────────────────────────────

    @PostMapping("/grievances")
    public ResponseEntity<GrievanceResponse> submitGrievance(
            @Valid @RequestBody GrievanceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                grievanceService.submitGrievance(request, userDetails.getUsername()));
    }

    @GetMapping("/grievances/my")
    public ResponseEntity<List<GrievanceResponse>> getMyGrievances(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                grievanceService.getMyGrievances(userDetails.getUsername()));
    }

    @GetMapping("/grievances/{id}")
    public ResponseEntity<GrievanceResponse> getGrievanceById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(grievanceService.getGrievanceById(id));
    }

    @PutMapping("/grievances/{id}/reopen")
    public ResponseEntity<ApiResponse> reopenGrievance(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                grievanceService.reopenGrievance(id, userDetails.getUsername()));
    }

    @PostMapping("/grievances/{id}/upvote")
    public ResponseEntity<ApiResponse> upvoteGrievance(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                grievanceService.upvoteGrievance(id, userDetails.getUsername()));
    }

    @DeleteMapping("/grievances/{id}/upvote")
    public ResponseEntity<ApiResponse> removeUpvote(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                grievanceService.removeUpvote(id, userDetails.getUsername()));
    }

    // ─────────────────────────────────────────
    //  OFFICER ENDPOINTS
    // ─────────────────────────────────────────

    @GetMapping("/officer/grievances")
    public ResponseEntity<List<GrievanceResponse>> getOfficerGrievances(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                grievanceService.getOfficerGrievances(userDetails.getUsername()));
    }

    @PutMapping("/officer/grievances/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                grievanceService.updateStatus(id, request, userDetails.getUsername()));
    }
}