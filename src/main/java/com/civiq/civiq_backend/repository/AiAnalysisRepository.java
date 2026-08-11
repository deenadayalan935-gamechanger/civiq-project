package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.AiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, UUID> {

    Optional<AiAnalysis> findByGrievanceId(UUID grievanceId);

    boolean existsByGrievanceId(UUID grievanceId);
}