package com.railtrack.ai.service.impl;

import com.railtrack.ai.prompt.PromptBuilder;
import com.railtrack.ai.service.AiChatService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class AiChatServiceImpl implements AiChatService {

    private final ChatModel chatModel;

    // Spring Boot automatically injects the configured Google GenAI ChatModel here
    public AiChatServiceImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String chat(String prompt) {

        if (prompt == null || prompt.trim().isEmpty()) {
            return "Please enter your railway-related question.";
        }

        try {

            String finalPrompt = PromptBuilder.buildAssistantPrompt(prompt);

            return chatModel.call(finalPrompt);

        } catch (Exception e) {

            return "Sorry, I couldn't process your request right now. Please try again later.";
        }
    }
}