package com.civiq.civiq_backend.controller;

import com.civiq.civiq_backend.dto.response.ApiResponse;
import com.civiq.civiq_backend.dto.response.GrievanceResponse;
import com.civiq.civiq_backend.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final GrievanceService grievanceService;

    // City-wide grievance overview
    @GetMapping("/grievances")
    public ResponseEntity<List<GrievanceResponse>> getAllGrievances() {
        return ResponseEntity.ok(grievanceService.getAllGrievances());
    }

    // SLA breach report
    @GetMapping("/analytics/sla")
    public ResponseEntity<List<GrievanceResponse>> getBreachedSlaGrievances() {
        return ResponseEntity.ok(grievanceService.getBreachedSlaGrievances());
    }

    // Department performance
    @GetMapping("/analytics/departments")
    public ResponseEntity<ApiResponse> getDepartmentPerformance() {
        return ResponseEntity.ok(
                new ApiResponse(true, "Department analytics coming soon"));
    }

    // Heatmap data
    @GetMapping("/analytics/heatmap")
    public ResponseEntity<ApiResponse> getHeatmapData() {
        return ResponseEntity.ok(
                new ApiResponse(true, "Heatmap data coming soon"));
    }
}