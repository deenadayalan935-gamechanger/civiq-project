package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.GrievanceStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrievanceStatusLogRepository extends JpaRepository<GrievanceStatusLog, UUID> {

    List<GrievanceStatusLog> findByGrievanceIdOrderByChangedAtAsc(UUID grievanceId);
}