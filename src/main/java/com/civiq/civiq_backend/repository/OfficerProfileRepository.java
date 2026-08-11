package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.OfficerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfficerProfileRepository extends JpaRepository<OfficerProfile, UUID> {

    Optional<OfficerProfile> findByUserId(UUID userId);

    Optional<OfficerProfile> findByDepartmentIdAndWardId(UUID departmentId, UUID wardId);

    boolean existsByUserId(UUID userId);

    boolean existsByEmployeeCode(String employeeCode);
}