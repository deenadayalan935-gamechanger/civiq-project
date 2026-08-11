package com.civiq.civiq_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "grievance_upvotes",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_citizen_grievance",
            columnNames = {"citizen_id", "grievance_id"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrievanceUpvote {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grievance_id", nullable = false)
    private Grievance grievance;

    @Column(name = "upvoted_at", updatable = false)
    private LocalDateTime upvotedAt;

    @PrePersist
    protected void onCreate() {
        upvotedAt = LocalDateTime.now();
    }
}