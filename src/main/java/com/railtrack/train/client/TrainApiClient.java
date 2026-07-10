package com.railtrack.train.client;

import com.railtrack.train.config.TrainApiProperties;
import com.railtrack.train.dto.response.TrainSearchResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TrainApiClient {

    private final TrainApiProperties properties;

    public TrainApiClient(TrainApiProperties properties) {
        this.properties = properties;
    }

    public TrainSearchResponse searchTrains(String from, String to) {

        WebClient webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + properties.getApiKey()
                )
                .build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/legacy/trains/between")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(TrainSearchResponse.class)
                .block();
    }
}