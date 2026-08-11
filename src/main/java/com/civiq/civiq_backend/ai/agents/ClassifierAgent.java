package com.civiq.civiq_backend.ai.agents;

import com.civiq.civiq_backend.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassifierAgent {

    private final AiService aiService;

    public String classify(String title, String description) {

        String systemPrompt = """
                You are an AI agent for CIVIQ, a civic grievance platform in Chennai, India.
                Your job is to classify a citizen's complaint into exactly ONE category.
                
                Available categories:
                - ROAD_DAMAGE (potholes, broken roads, damaged footpath)
                - STREET_LIGHT (broken lights, no electricity in street)
                - WATER_ISSUE (leaking pipes, no water supply, contaminated water)
                - BUS_SHELTER (damaged or missing bus shelters)
                - GARBAGE (waste not collected, overflowing bins)
                - DRAINAGE (blocked drains, sewage overflow)
                - PARK_MAINTENANCE (damaged park equipment, overgrown areas)
                - OTHER (anything that doesn't fit above)
                
                Rules:
                - Respond with ONLY the category code. Nothing else.
                - No explanation. No punctuation. Just the category.
                - Example response: ROAD_DAMAGE
                """;

        String userPrompt = String.format("""
                Complaint Title: %s
                Complaint Description: %s
                
                What is the category?
                """, title, description);

        return aiService.callAi(systemPrompt, userPrompt);
    }
}