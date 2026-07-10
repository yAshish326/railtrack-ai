package com.railtrack.train.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TrainApiProperties {

    @Value("${railway.api.base-url}")
    private String baseUrl;

    @Value("${railway.api.key}")
    private String apiKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}