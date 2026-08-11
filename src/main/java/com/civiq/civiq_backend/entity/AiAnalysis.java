package com.civiq.civiq_backend.entity;

import com.civiq.civiq_backend.enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_analysis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysis {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grievance_id", nullable = false, unique = true)
    private Grievance grievance;

    @Column(name = "predicted_category", length = 50)
    private String predictedCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "predicted_priority")
    private Priority predictedPriority;

    @Column(name = "predicted_department", length = 100)
    private String predictedDepartment;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "pipeline_status", nullable = false)
    private PipelineStatus pipelineStatus = PipelineStatus.PENDING;

    @Column(name = "analysed_at")
    private LocalDateTime analysedAt;

    @PrePersist
    protected void onCreate() {
        pipelineStatus = PipelineStatus.PENDING;
    }

    public enum PipelineStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
}