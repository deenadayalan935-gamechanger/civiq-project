package com.civiq.civiq_backend.ai.agents;

import com.civiq.civiq_backend.entity.Department;
import com.civiq.civiq_backend.entity.OfficerProfile;
import com.civiq.civiq_backend.repository.DepartmentRepository;
import com.civiq.civiq_backend.repository.OfficerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RouterAgent {

    private final DepartmentRepository departmentRepository;
    private final OfficerProfileRepository officerProfileRepository;

    public Department findDepartment(String categoryCode) {
        return departmentRepository
                .findByCategoryCode(categoryCode)
                .orElse(null);
    }

    public OfficerProfile findOfficer(UUID departmentId, UUID wardId) {
        return officerProfileRepository
                .findByDepartmentIdAndWardId(departmentId, wardId)
                .orElse(null);
    }
}