package com.civiq.civiq_backend.ai.pipeline;

import com.civiq.civiq_backend.ai.agents.ClassifierAgent;
import com.civiq.civiq_backend.ai.agents.PriorityAgent;
import com.civiq.civiq_backend.ai.agents.RouterAgent;
import com.civiq.civiq_backend.ai.agents.SummaryAgent;
import com.civiq.civiq_backend.entity.AiAnalysis;
import com.civiq.civiq_backend.entity.Department;
import com.civiq.civiq_backend.entity.Grievance;
import com.civiq.civiq_backend.entity.OfficerProfile;
import com.civiq.civiq_backend.enums.Priority;
import com.civiq.civiq_backend.repository.AiAnalysisRepository;
import com.civiq.civiq_backend.repository.GrievanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class GrievancePipeline {

    private final ClassifierAgent classifierAgent;
    private final PriorityAgent priorityAgent;
    private final RouterAgent routerAgent;
    private final SummaryAgent summaryAgent;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final GrievanceRepository grievanceRepository;

    @Async
    public void process(Grievance grievance) {

        log.info("AI Pipeline started for grievance: {}", grievance.getId());

        // Create initial AI analysis record with PENDING status
        AiAnalysis analysis = AiAnalysis.builder()
                .grievance(grievance)
                .pipelineStatus(AiAnalysis.PipelineStatus.PENDING)
                .build();
        aiAnalysisRepository.save(analysis);

        try {

            // ─────────────────────────────────────────
            //  AGENT 1 — CLASSIFIER
            // ─────────────────────────────────────────
            log.info("Agent 1 - Classifying grievance...");
            String predictedCategory = classifierAgent.classify(
                    grievance.getTitle(),
                    grievance.getDescription()
            );
            log.info("Classified as: {}", predictedCategory);

            // ─────────────────────────────────────────
            //  AGENT 2 — PRIORITY
            // ─────────────────────────────────────────
            log.info("Agent 2 - Assessing priority...");
            Priority predictedPriority = priorityAgent.assessPriority(
                    grievance.getTitle(),
                    grievance.getDescription(),
                    predictedCategory
            );
            LocalDateTime slaDeadline = priorityAgent
                    .calculateSlaDeadline(predictedPriority);
            log.info("Priority: {} | SLA Deadline: {}", predictedPriority, slaDeadline);

            // ─────────────────────────────────────────
            //  AGENT 3 — ROUTER
            // ─────────────────────────────────────────
            log.info("Agent 3 - Routing to department and officer...");
            Department department = routerAgent.findDepartment(predictedCategory);
            OfficerProfile officer = null;

            if (department != null) {
                officer = routerAgent.findOfficer(
                        department.getId(),
                        grievance.getWard().getId()
                );
            }

            // Update grievance with routing info
            grievance.setCategoryCode(predictedCategory);
            grievance.setSlaDeadline(slaDeadline);

            if (department != null) {
                grievance.setDepartment(department);
                log.info("Routed to department: {}", department.getName());
            }

            if (officer != null) {
                grievance.setAssignedOfficer(officer.getUser());
                log.info("Assigned to officer: {}", officer.getUser().getFullName());
            }

            grievanceRepository.save(grievance);

            // ─────────────────────────────────────────
            //  AGENT 4 — SUMMARIZER
            // ─────────────────────────────────────────
            log.info("Agent 4 - Generating summary...");
            String aiSummary = summaryAgent.generateSummary(
                    grievance.getTitle(),
                    grievance.getDescription(),
                    predictedCategory,
                    predictedPriority,
                    grievance.getWard().getName()
            );
            log.info("Summary generated successfully");

            // ─────────────────────────────────────────
            //  SAVE COMPLETED ANALYSIS
            // ─────────────────────────────────────────
            analysis.setPredictedCategory(predictedCategory);
            analysis.setPredictedPriority(predictedPriority);
            analysis.setPredictedDepartment(
                    department != null ? department.getName() : "UNASSIGNED");
            analysis.setAiSummary(aiSummary);
            analysis.setPipelineStatus(AiAnalysis.PipelineStatus.COMPLETED);
            analysis.setAnalysedAt(LocalDateTime.now());
            aiAnalysisRepository.save(analysis);

            log.info("AI Pipeline completed for grievance: {}", grievance.getId());

        } catch (Exception e) {
            // If anything fails, mark pipeline as FAILED
            log.error("AI Pipeline failed for grievance: {}", grievance.getId(), e);
            analysis.setPipelineStatus(AiAnalysis.PipelineStatus.FAILED);
            aiAnalysisRepository.save(analysis);
        }
    }
}