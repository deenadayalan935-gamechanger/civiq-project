package com.civiq.civiq_backend.dto.response;

import com.civiq.civiq_backend.enums.GrievanceStatus;
import com.civiq.civiq_backend.enums.Priority;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GrievanceResponse {

    private UUID id;
    private String title;
    private String description;
    private String categoryCode;
    private String wardName;
    private String departmentName;
    private String assignedOfficerName;
    private Double latitude;
    private Double longitude;
    private GrievanceStatus currentStatus;
    private Priority aiPriority;
    private String aiSummary;
    private Integer upvoteCount;
    private LocalDateTime slaDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}