package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {

    Optional<Zone> findByZoneNumber(Integer zoneNumber);

    boolean existsByZoneNumber(Integer zoneNumber);
}