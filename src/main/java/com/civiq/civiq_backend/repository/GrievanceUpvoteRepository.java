package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.GrievanceUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GrievanceUpvoteRepository extends JpaRepository<GrievanceUpvote, UUID> {

    Optional<GrievanceUpvote> findByCitizenIdAndGrievanceId(UUID citizenId, UUID grievanceId);

    boolean existsByCitizenIdAndGrievanceId(UUID citizenId, UUID grievanceId);

    int countByGrievanceId(UUID grievanceId);
}