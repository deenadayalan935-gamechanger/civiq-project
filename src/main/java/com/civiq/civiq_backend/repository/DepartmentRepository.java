package com.civiq.civiq_backend.repository;

import com.civiq.civiq_backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Optional<Department> findByCategoryCode(String categoryCode);

    boolean existsByCategoryCode(String categoryCode);
}