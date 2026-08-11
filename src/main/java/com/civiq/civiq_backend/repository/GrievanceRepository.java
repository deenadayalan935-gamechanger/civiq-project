package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.Grievance;
import com.civiq.civiq_backend.enums.GrievanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, UUID> {

    // Citizen - view their own grievances
    List<Grievance> findByCitizenId(UUID citizenId);

    // Officer - ward locked Kanban board
    List<Grievance> findByWardIdAndDepartmentId(UUID wardId, UUID departmentId);

    // Officer - filter by status on Kanban board
    List<Grievance> findByWardIdAndDepartmentIdAndCurrentStatus(
        UUID wardId,
        UUID departmentId,
        GrievanceStatus status
    );

    // Admin - filter city wide grievances by status
    List<Grievance> findByCurrentStatus(GrievanceStatus status);

    // Admin - heatmap data (lat, lng, status)
    @Query("SELECT g FROM Grievance g WHERE g.currentStatus != :excludedStatus")
    List<Grievance> findAllExcludingStatus(@Param("excludedStatus") GrievanceStatus status);

    // SLA breach detection
    @Query("SELECT g FROM Grievance g WHERE g.slaDeadline < CURRENT_TIMESTAMP " +
           "AND g.currentStatus NOT IN ('RESOLVED')")
    List<Grievance> findBreachedSlaGrievances();
}