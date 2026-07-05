package com.railtrack.ai.service.impl;

import com.railtrack.ai.dto.AiChatResponse;
import com.railtrack.ai.prompt.PromptBuilder;
import com.railtrack.ai.service.AiService;
import com.railtrack.pnr.dto.response.Passenger;
import com.railtrack.pnr.dto.response.PnrResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // =========================================================================
    // General AI Chat
    // =========================================================================

    @Override
    public AiChatResponse chat(String message) {

        String aiResponse = chatClient
                .prompt(message)
                .call()
                .content();

        return new AiChatResponse(aiResponse);
    }

    // =========================================================================
    // AI Powered PNR Explanation
    // =========================================================================

    @Override
    public String explainPnr(PnrResponse response) {
        String prompt = PromptBuilder.buildPnrPrompt(response);

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}