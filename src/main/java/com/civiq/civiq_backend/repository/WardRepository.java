package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> {

    Optional<Ward> findByWardNumber(Integer wardNumber);

    List<Ward> findByZoneId(UUID zoneId);

    boolean existsByWardNumber(Integer wardNumber);
}