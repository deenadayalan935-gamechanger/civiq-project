package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.GrievanceMedia;
import com.civiq.civiq_backend.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GrievanceMediaRepository extends JpaRepository<GrievanceMedia, UUID> {

    List<GrievanceMedia> findByGrievanceId(UUID grievanceId);

    List<GrievanceMedia> findByGrievanceIdAndMediaType(UUID grievanceId, MediaType mediaType);
}