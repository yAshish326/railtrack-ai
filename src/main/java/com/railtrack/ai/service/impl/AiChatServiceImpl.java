package com.railtrack.ai.service.impl;

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
        try {
            if (prompt == null || prompt.trim().isEmpty()) {
                return "Prompt cannot be empty.Please enter the Prompt";
            }
            // Executes the model call using Spring AI's native orchestration layer
            return chatModel.call(prompt);
        } catch (Exception e) {
            return "❌ AI Analytics Service Error: " + e.getMessage();
        }
    }
}