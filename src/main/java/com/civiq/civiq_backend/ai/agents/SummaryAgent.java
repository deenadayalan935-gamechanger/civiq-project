package com.civiq.civiq_backend.ai.agents;

import com.civiq.civiq_backend.ai.AiService;
import com.civiq.civiq_backend.enums.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SummaryAgent {

    private final AiService aiService;

    public String generateSummary(String title,
                                   String description,
                                   String category,
                                   Priority priority,
                                   String wardName) {

        String systemPrompt = """
                You are an AI summary agent for CIVIQ, a civic grievance platform 
                in Chennai, India.
                Your job is to generate a clear, professional work order summary 
                for a field engineer.
                
                Rules:
                - Write in 2-3 sentences maximum
                - Be specific and actionable
                - Include location context if available
                - Use professional language suitable for a government work order
                - Do not include priority or category labels in your response
                - Focus on WHAT needs to be done and WHERE
                """;

        String userPrompt = String.format("""
                Complaint Title: %s
                Complaint Description: %s
                Category: %s
                Priority: %s
                Ward: %s
                
                Generate a professional work order summary for the field engineer:
                """, title, description, category, priority.name(), wardName);

        return aiService.callAi(systemPrompt, userPrompt);
    }
}