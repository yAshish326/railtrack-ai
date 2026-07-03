package com.railtrack.pnr.client;

import com.railtrack.common.config.RapidApiProperties;
import com.railtrack.pnr.dto.response.PnrResponse;
import com.railtrack.common.exception.RailwayApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RailwayApiClient {

    private final WebClient webClient;
    private final RapidApiProperties properties;

    public RailwayApiClient(WebClient webClient,
                            RapidApiProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public PnrResponse getPnrStatus(String pnrNumber) {

        try {

            return webClient
                    .get()
                    .uri(properties.getBaseUrl() + "/getPNRStatus/" + pnrNumber)
                    .header("x-rapidapi-key", properties.getKey())
                    .header("x-rapidapi-host", properties.getHost())
                    .retrieve()
                    .bodyToMono(PnrResponse.class)
                    .block();

        } catch (Exception ex) {

            throw new RailwayApiException(
                    "Unable to fetch PNR details from Railway API.",
                    ex
            );
        }
    }
}