package com.civiq.civiq_backend.dto.request;

import com.civiq.civiq_backend.enums.GrievanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private GrievanceStatus newStatus;

    private String remarks;
}