package com.civiq.civiq_backend.ai.agents;

import com.civiq.civiq_backend.ai.AiService;
import com.civiq.civiq_backend.enums.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PriorityAgent {

    private final AiService aiService;

    public Priority assessPriority(String title, String description, String category) {

        String systemPrompt = """
                You are an AI priority assessment agent for CIVIQ, a civic grievance 
                platform in Chennai, India.
                Your job is to assess the urgency of a citizen complaint.
                
                Priority levels:
                - CRITICAL (immediate danger to public safety, e.g. burst water main 
                  flooding road, live electrical wire down, major road collapse)
                - HIGH (significant disruption, e.g. entire street with no lights, 
                  large pothole on main road, sewage overflow)
                - MEDIUM (moderate issue, e.g. damaged footpath, flickering street light,
                  overflowing garbage bin)
                - LOW (minor issue, e.g. faded road marking, small crack in footpath,
                  minor park damage)
                
                Rules:
                - Respond with ONLY the priority level. Nothing else.
                - No explanation. No punctuation. Just the priority.
                - Example response: HIGH
                """;

        String userPrompt = String.format("""
                Category: %s
                Complaint Title: %s
                Complaint Description: %s
                
                What is the priority level?
                """, category, title, description);

        String result = aiService.callAi(systemPrompt, userPrompt);

        try {
            return Priority.valueOf(result.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Priority.MEDIUM;
        }
    }

    public LocalDateTime calculateSlaDeadline(Priority priority) {
        LocalDateTime now = LocalDateTime.now();
        return switch (priority) {
            case CRITICAL -> now.plusHours(4);
            case HIGH -> now.plusHours(24);
            case MEDIUM -> now.plusDays(3);
            case LOW -> now.plusDays(7);
        };
    }
}