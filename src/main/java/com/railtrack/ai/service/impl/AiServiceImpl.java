package com.railtrack.ai.service.impl;

import com.railtrack.ai.dto.AiChatResponse;
import com.railtrack.ai.dto.AiTrainRecommendationResponse;
import com.railtrack.ai.dto.response.AiRecommendationSummary;
import com.railtrack.ai.prompt.PromptBuilder;
import com.railtrack.ai.service.AiHistoryService;
import com.railtrack.ai.service.AiService;
import com.railtrack.auth.entity.User;
import com.railtrack.auth.service.UserService;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.train.dto.response.RecommendedTrainResponse;
import com.railtrack.train.dto.response.Train;
import com.railtrack.train.service.TrainService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final AiHistoryService aiHistoryService;
    private final UserService userService;
    private final TrainService trainService;

    public AiServiceImpl(ChatClient.Builder chatClientBuilder,
                         AiHistoryService aiHistoryService,
                         UserService userService,
                         TrainService trainService) {

        this.chatClient = chatClientBuilder.build();
        this.aiHistoryService = aiHistoryService;
        this.userService = userService;
        this.trainService = trainService;
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

        String aiResponse = chatClient
                .prompt(prompt)
                .call()
                .content();

        User user = userService.getAuthenticatedUser();

        aiHistoryService.saveHistory(
                user,
                response.getData().getPnrNumber(),
                prompt,
                aiResponse
        );

        return aiResponse;
    }

    // =========================================================================
    // AI Train Recommendation
    // =========================================================================

    @Override
    public AiTrainRecommendationResponse recommendTrain(String from, String to) {

        RecommendedTrainResponse response =
                trainService.getRecommendedTrain(from, to);

        Train bestTrain = response.getBestTrain();

        if (bestTrain == null) {

            AiRecommendationSummary summary =
                    new AiRecommendationSummary(
                            "No Train Found",
                            "Sorry, no trains are available for this route.",
                            List.of(),
                            List.of()
                    );

            return new AiTrainRecommendationResponse(
                    null,
                    summary,
                    response.getTrainSearchResponse(),
                    LocalDateTime.now(),
                    "Gemini 2.5 Flash"
            );
        }

        String prompt = """
                You are an Indian Railway travel expert.

                The backend has already selected the best train.

                Explain in less than 80 words why this train is the best choice.

                Mention:
                - Train Name
                - Train Number
                - Travel Time
                - Train Type
                - Running Days
                - Available Classes

                Keep the response short, friendly and simple.

                Train Name: %s
                Train Number: %s
                Train Type: %s
                Travel Time: %s
                Running Days: %s
                Available Classes: %s
                """.formatted(
                bestTrain.getName(),
                bestTrain.getNumber(),
                bestTrain.getType(),
                bestTrain.getJourneySegment().getTravelTime(),
                bestTrain.getRunDays(),
                bestTrain.getAvailableClasses()
        );

        String aiResponse = chatClient
                .prompt(prompt)
                .call()
                .content();

        AiRecommendationSummary summary =
                new AiRecommendationSummary(
                        "Best Train Recommendation",
                        aiResponse,
                        List.of(),
                        List.of()
                );

        return new AiTrainRecommendationResponse(
                bestTrain,
                summary,
                response.getTrainSearchResponse(),
                LocalDateTime.now(),
                "Gemini 2.5 Flash"
        );
    }
}