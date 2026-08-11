package com.civiq.civiq_backend.config;

import com.civiq.civiq_backend.entity.Department;
import com.civiq.civiq_backend.entity.Ward;
import com.civiq.civiq_backend.entity.Zone;
import com.civiq.civiq_backend.repository.DepartmentRepository;
import com.civiq.civiq_backend.repository.WardRepository;
import com.civiq.civiq_backend.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final WardRepository wardRepository;
    private final ZoneRepository zoneRepository;

    @Override
    public void run(String... args) {
        try {
            seedZonesAndWards();
            seedDepartments();
        } catch (Exception e) {
            log.error("Seeding failed: {}", e.getMessage());
        }
    }

    private void seedZonesAndWards() {
        if (wardRepository.count() > 0) {
            log.info("Wards already seeded. Skipping.");
            return;
        }

        log.info("Seeding zones and wards...");

        Zone zone = Zone.builder()
                .zoneNumber(14)
                .name("Perungudi Zone")
                .build();
        zoneRepository.save(zone);

        Ward ward = Ward.builder()
                .wardNumber(184)
                .name("Pallikaranai")
                .zone(zone)
                .build();
        wardRepository.save(ward);

        log.info("Zones and wards seeded successfully.");
    }

    private void seedDepartments() {
        if (departmentRepository.existsByCategoryCode("ROAD_DAMAGE")) {
            log.info("Departments already seeded. Skipping.");
            return;
        }

        log.info("Seeding departments...");

        saveIfNotExists("GCC Roads Division", "ROAD_DAMAGE", "Handles road damage");
        saveIfNotExists("GCC Electrical Department", "STREET_LIGHT", "Handles street lights");
        saveIfNotExists("Metro Water CMWSSB", "WATER_ISSUE", "Handles water issues");
        saveIfNotExists("GCC Bus Route Roads", "BUS_SHELTER", "Handles bus shelters");
        saveIfNotExists("GCC Sanitation", "GARBAGE", "Handles garbage");
        saveIfNotExists("GCC Storm Water Drains", "DRAINAGE", "Handles drainage");
        saveIfNotExists("GCC Parks Division", "PARK_MAINTENANCE", "Handles parks");
        saveIfNotExists("GCC General", "OTHER", "Handles other issues");

        log.info("Departments seeded successfully.");
    }

    private void saveIfNotExists(String name, String categoryCode, String description) {
        if (!departmentRepository.existsByCategoryCode(categoryCode)) {
            departmentRepository.save(
                Department.builder()
                    .name(name)
                    .categoryCode(categoryCode)
                    .description(description)
                    .build()
            );
        }
    }
}