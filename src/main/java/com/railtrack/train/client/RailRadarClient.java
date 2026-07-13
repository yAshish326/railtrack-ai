package com.railtrack.train.client;

import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.common.exception.RailRadarClientException;
import com.railtrack.train.config.TrainApiProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/** WebClient gateway for documented RailRadar v1 APIs. */
@Component
public class RailRadarClient {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private final WebClient webClient;
    private final TrainApiProperties properties;
    public RailRadarClient(WebClient webClient, TrainApiProperties properties) { this.properties = properties; this.webClient = webClient.mutate().baseUrl(properties.getBaseUrl()).build(); }

    @Cacheable(value = "railRadarTrainDetails", key = "#number + ':' + #journeyDate + ':' + #dataType + ':' + #dataProvider + ':' + #userId")
    public RailRadarResponse trainDetails(String number, LocalDate journeyDate, String dataType, String dataProvider, String userId) { return get("/legacy/trains/" + number, params("journeyDate", journeyDate, "dataType", dataType, "dataProvider", dataProvider, "userId", userId)); }
    @Cacheable(value = "railRadarLiveTrain", key = "#number + ':' + #date + ':' + #haltsOnly + ':' + #geometry + ':' + #format + ':' + #includeCoordinates")
    public RailRadarResponse liveTrain(String number, LocalDate date, boolean haltsOnly, boolean geometry, String format, boolean includeCoordinates) { return get("/trains/" + number + "/live", params("date", date, "haltsOnly", haltsOnly, "geometry", geometry, "format", format, "includeCoordinates", includeCoordinates)); }
    @Cacheable(value = "railRadarRoute", key = "#number + ':' + #format + ':' + #stops")
    public RailRadarResponse route(String number, String format, boolean stops) { return get("/trains/" + number + "/route", params("format", format, "stops", stops)); }
    @Cacheable(value = "railRadarJourney", key = "#from + ':' + #to + ':' + #date + ':' + #live + ':' + #byCity + ':' + #type + ':' + #category")
    public RailRadarResponse betweenStations(String from, String to, LocalDate date, boolean live, boolean byCity, String type, String category) { return get("/trains/between/" + from + "/" + to, params("date", date, "live", live, "byCity", byCity, "type", type, "category", category)); }
    @Cacheable(value = "railRadarStationBoard", key = "#code + ':' + #includeIntermediate")
    public RailRadarResponse stationBoard(String code, boolean includeIntermediate) { return get("/stations/" + code + "/trains", params("includeIntermediate", includeIntermediate)); }
    @Cacheable(value = "railRadarStationLiveBoard", key = "#code + ':' + #hours + ':' + #includeIntermediate")
    public RailRadarResponse stationLiveBoard(String code, int hours, boolean includeIntermediate) { return get("/stations/" + code + "/live", params("hours", hours, "includeIntermediate", includeIntermediate)); }

    private RailRadarResponse get(String path, Map<String, Object> parameters) {
        try {
            return webClient.get().uri(builder -> { var uri = builder.path(path); parameters.forEach((key, value) -> { if (value != null) uri.queryParam(key, value); }); return uri.build(); })
                    .headers(headers -> headers.setBearerAuth(propertiesKey()))
                    .retrieve()
                    .onStatus(status -> status.value() == 404, response -> response.createException().map(error -> new RailRadarClientException(HttpStatus.NOT_FOUND, "RailRadar resource was not found.", error)))
                    .onStatus(status -> status.isError(), response -> response.createException().map(error -> new RailRadarClientException(HttpStatus.BAD_GATEWAY, "RailRadar request failed with status " + error.getStatusCode().value() + ".", error)))
                    .bodyToMono(RailRadarResponse.class).timeout(REQUEST_TIMEOUT).block();
        } catch (RailRadarClientException exception) { throw exception; }
        catch (Exception exception) { throw new RailRadarClientException(HttpStatus.GATEWAY_TIMEOUT, "RailRadar did not respond within the configured timeout.", exception); }
    }
    private String propertiesKey() { return properties.getApiKey(); }
    private Map<String, Object> params(Object... entries) { Map<String, Object> result = new LinkedHashMap<>(); for (int i = 0; i < entries.length; i += 2) result.put((String) entries[i], entries[i + 1]); return result; }
}
