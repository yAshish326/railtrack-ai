package com.railtrack.train.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.railtrack.common.dto.RailRadarResponse;
import com.railtrack.train.dto.response.JourneyResponse;
import com.railtrack.train.dto.response.LiveStationBoardResponse;
import com.railtrack.train.dto.response.LiveTrainResponse;
import com.railtrack.train.dto.response.RouteStationResponse;
import com.railtrack.train.dto.response.StationBoardResponse;
import com.railtrack.train.dto.response.StationBoardTrainResponse;
import com.railtrack.train.dto.response.TrainDetailsResponse;
import com.railtrack.train.dto.response.TrainRouteResponse;
import com.railtrack.train.dto.response.TrainSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Translates raw RailRadar {@link RailRadarResponse} payloads (provider
 * JSON, {@link JsonNode}) into business DTOs owned by this application.
 * Controllers and services must never return {@link RailRadarResponse} (or
 * any {@link JsonNode}) directly to the frontend - this is the single place
 * that boundary is crossed.
 *
 * <p><b>Field mappings below are verified against RailRadar's published API
 * reference (railradar.in/docs) as of 2026-07-17</b>, not guessed. A few
 * important gaps in what RailRadar actually returns, versus what a richer
 * UI might want, are called out per method - these are provider
 * limitations, not mapping bugs:
 * <ul>
 *   <li>{@link #mapTrainRoute} - the route-geometry endpoint returns only
 *       station code/name/lat/lng per stop. No arrival/departure/platform/
 *       day/distance is available there (that data lives on the separate
 *       train-details endpoint instead). {@code trainName} and
 *       {@code totalDistanceKm} are simply not returned by this endpoint.</li>
 *   <li>{@link #mapStationBoard} - the static station board has no
 *       delay/platform/status/expected-time fields; those only exist on the
 *       live station board.</li>
 *   <li>{@link #mapLiveStationBoard} - RailRadar has no "cancelled" concept;
 *       {@code cancelledTrains} will always be empty.</li>
 *   <li>{@link #mapBetweenStations} - RailRadar has no quota/class filter or
 *       "available classes" field at all.</li>
 * </ul>
 */
@Component
public class RailRadarMapper {

    private static final Logger log = LoggerFactory.getLogger(RailRadarMapper.class);

    // ---------------------------------------------------------------
    // Train Details - GET /v1/trains/{number}
    // data: { train: {number,name,type,category,source{code,name},
    //         destination{code,name},runDays[],distance,duration,
    //         avgSpeed,maxSpeed,totalHalts,returnTrain,coachPosition},
    //         route: [{sequence,station{code,name},arrival,departure,
    //         arrivalDay,departureDay,distance,isHalt,platform,
    //         speedToNextStationKmph}] }
    // ---------------------------------------------------------------
    public TrainDetailsResponse mapTrainDetails(RailRadarResponse response) {

        JsonNode data = validData(response, "trainDetails");
        if (data == null) {
            return null;
        }

        JsonNode train = data.get("train");
        if (train == null || train.isNull()) {
            return null;
        }

        JsonNode source = train.get("source");
        JsonNode destination = train.get("destination");

        return TrainDetailsResponse.builder()
                .trainNumber(getText(train, "number"))
                .trainName(getText(train, "name"))
                .trainType(getText(train, "type"))
                .sourceStationCode(getText(source, "code"))
                .sourceStationName(getText(source, "name"))
                .destinationStationCode(getText(destination, "code"))
                .destinationStationName(getText(destination, "name"))
                .distanceKm(getDouble(train, "distance"))
                .travelTimeMinutes(getInt(train, "duration"))
                .totalHalts(getInt(train, "totalHalts"))
                .runningDays(extractStringArray(train, "runDays"))
                .build();
    }

    // ---------------------------------------------------------------
    // Live Train - GET /v1/trains/{number}/live
    // data: { trainNumber,trainName,startDate,lastUpdatedAt,status,
    //         delayMinutes, currentLocation{stationCode,sequence,status,
    //         speedKmh,...}, previousHalt{stationCode,stationName,...},
    //         nextHalt{stationCode,stationName,...},
    //         route:[{sequence,stationCode,stationName,lat,lng,
    //         scheduledArrival,scheduledDeparture,actualArrival,
    //         actualDeparture,platform,...}] }
    // Coordinates/platform/expected-actual times aren't top-level - they
    // live on the matching entry in `route[]`, keyed by stationCode.
    // ---------------------------------------------------------------
    public LiveTrainResponse mapLiveTrain(RailRadarResponse response) {

        JsonNode data = validData(response, "liveTrain");
        if (data == null) {
            return null;
        }

        Map<String, JsonNode> routeByStationCode = indexByField(data.get("route"), "stationCode");

        JsonNode currentLocation = data.get("currentLocation");
        JsonNode previousHalt = data.get("previousHalt");
        JsonNode nextHalt = data.get("nextHalt");

        String currentStationCode = getText(currentLocation, "stationCode");
        String nextStationCode = getText(nextHalt, "stationCode");

        JsonNode currentRouteEntry = routeByStationCode.get(currentStationCode);
        JsonNode nextRouteEntry = routeByStationCode.get(nextStationCode);

        return LiveTrainResponse.builder()
                .trainNumber(getText(data, "trainNumber"))
                .trainName(getText(data, "trainName"))
                .previousStation(getText(previousHalt, "stationName"))
                .currentStation(currentStationCode != null ? currentStationCode
                        : getText(currentRouteEntry, "stationName"))
                .nextStation(getText(nextHalt, "stationName"))
                .latitude(getDouble(currentRouteEntry, "lat"))
                .longitude(getDouble(currentRouteEntry, "lng"))
                .delayMinutes(getInt(data, "delayMinutes"))
                .expectedArrival(getText(nextRouteEntry, "scheduledArrival"))
                .actualArrival(getText(nextRouteEntry, "actualArrival"))
                .platform(getText(currentRouteEntry, "platform"))
                .speedKmph(getDouble(currentLocation, "speedKmh"))
                .runningStatus(getText(data, "status"))
                .lastUpdatedAt(getText(data, "lastUpdatedAt"))
                .build();
    }

    // ---------------------------------------------------------------
    // Train Route - GET /v1/trains/{number}/route
    // data: { trainNumber, format, geojson{...}, stops:[{sequence,code,
    //         name,lat,lng}] }
    // This endpoint intentionally only returns geometry - no schedule
    // times, platform, halt duration, or trainName. Those fields stay
    // null here; combine with mapTrainDetails() if a caller needs both.
    // ---------------------------------------------------------------
    public TrainRouteResponse mapTrainRoute(RailRadarResponse response) {

        JsonNode data = validData(response, "trainRoute");
        if (data == null) {
            return null;
        }

        List<RouteStationResponse> stations = new ArrayList<>();
        JsonNode stops = data.get("stops");
        if (stops != null && stops.isArray()) {
            for (JsonNode stop : stops) {
                stations.add(RouteStationResponse.builder()
                        .sequence(getInt(stop, "sequence"))
                        .stationCode(getText(stop, "code"))
                        .stationName(getText(stop, "name"))
                        .latitude(getDouble(stop, "lat"))
                        .longitude(getDouble(stop, "lng"))
                        .build());
            }
        }

        return TrainRouteResponse.builder()
                .trainNumber(getText(data, "trainNumber"))
                .stations(stations)
                .build();
    }

    // ---------------------------------------------------------------
    // Between Stations - GET /v1/trains/between/{from}/{to}
    // data: { from{code,name}, to{code,name}, count, trains:[{train{number,
    //         name,type,runDays},from{departure,day,sequence},
    //         to{arrival,day,sequence},distance,duration,
    //         totalHaltsBetween,live{...}}] }
    // RailRadar has no quota/class filter or "available classes" field at
    // all - that list stays permanently empty here.
    // ---------------------------------------------------------------
    public JourneyResponse mapBetweenStations(RailRadarResponse response, String from, String to) {

        JsonNode data = validData(response, "betweenStations");
        if (data == null) {
            return JourneyResponse.builder()
                    .source(from).destination(to).totalTrains(0).trains(new ArrayList<>())
                    .build();
        }

        JsonNode fromStation = data.get("from");
        JsonNode toStation = data.get("to");
        String sourceName = fromStation != null ? getText(fromStation, "name") : from;
        String destinationName = toStation != null ? getText(toStation, "name") : to;

        List<TrainSummaryResponse> trains = new ArrayList<>();
        JsonNode trainList = data.get("trains");
        if (trainList != null && trainList.isArray()) {
            for (JsonNode entry : trainList) {
                JsonNode train = entry.get("train");
                JsonNode fromLeg = entry.get("from");
                JsonNode toLeg = entry.get("to");

                trains.add(TrainSummaryResponse.builder()
                        .trainNumber(getText(train, "number"))
                        .trainName(getText(train, "name"))
                        .trainType(getText(train, "type"))
                        .source(sourceName)
                        .destination(destinationName)
                        .departure(getText(fromLeg, "departure"))
                        .arrival(getText(toLeg, "arrival"))
                        .duration(getText(entry, "duration"))
                        .distanceKm(getDouble(entry, "distance"))
                        .runningDays(extractStringArray(train, "runDays"))
                        .availableClasses(new ArrayList<>())
                        .build());
            }
        }

        Integer count = getInt(data, "count");

        return JourneyResponse.builder()
                .source(sourceName)
                .destination(destinationName)
                .totalTrains(count != null ? count : trains.size())
                .trains(trains)
                .build();
    }

    // ---------------------------------------------------------------
    // Station Board - GET /v1/stations/{code}/trains
    // data: { station{code,name}, count, includeIntermediate,
    //         trains:[{train{number,name,type,source,destination,runDays},
    //         stop{sequence,arrival,departure,arrivalDay,departureDay,
    //         distance,stopType}}] }
    // Static board has no delay/platform/status/expected-time fields -
    // those only exist on the live station board below.
    // ---------------------------------------------------------------
    public StationBoardResponse mapStationBoard(RailRadarResponse response, String stationCode) {

        JsonNode data = validData(response, "stationBoard");
        List<StationBoardTrainResponse> trains = new ArrayList<>();
        String stationName = null;

        if (data != null) {
            JsonNode station = data.get("station");
            stationName = getText(station, "name");

            JsonNode trainList = data.get("trains");
            if (trainList != null && trainList.isArray()) {
                for (JsonNode entry : trainList) {
                    JsonNode train = entry.get("train");
                    JsonNode stop = entry.get("stop");
                    trains.add(StationBoardTrainResponse.builder()
                            .trainNumber(getText(train, "number"))
                            .trainName(getText(train, "name"))
                            .arrival(getText(stop, "arrival"))
                            .departure(getText(stop, "departure"))
                            .build());
                }
            }
        }

        Integer count = data != null ? getInt(data, "count") : null;

        return StationBoardResponse.builder()
                .stationCode(stationCode)
                .stationName(stationName)
                .date(null) // static board is not date-scoped; RailRadar returns no date field
                .totalTrains(count != null ? count : trains.size())
                .trains(trains)
                .build();
    }

    // ---------------------------------------------------------------
    // Live Station Board - GET /v1/stations/{code}/live
    // data: { station{code,name}, window{...}, count,
    //         trains:[{train{...},stop{sequence,arrival,departure,day,
    //         distance},live{type,expectedArrivalTime/expectedDepartureTime,
    //         platform,delayMinutes}}] }
    // RailRadar returns one flat list with a live.type of at-station,
    // upcoming, departed, or scheduled - grouping below is derived
    // client-side. There is no "cancelled" concept in the API, so that
    // bucket is always empty.
    // ---------------------------------------------------------------
    public LiveStationBoardResponse mapLiveStationBoard(RailRadarResponse response, String stationCode) {

        JsonNode data = validData(response, "liveStationBoard");

        List<StationBoardTrainResponse> arriving = new ArrayList<>();
        List<StationBoardTrainResponse> departing = new ArrayList<>();
        List<StationBoardTrainResponse> delayed = new ArrayList<>();
        List<StationBoardTrainResponse> cancelled = new ArrayList<>();

        if (data != null) {
            JsonNode trainList = data.get("trains");
            if (trainList != null && trainList.isArray()) {
                for (JsonNode entry : trainList) {
                    JsonNode train = entry.get("train");
                    JsonNode stop = entry.get("stop");
                    JsonNode live = entry.get("live");

                    StationBoardTrainResponse row = StationBoardTrainResponse.builder()
                            .trainNumber(getText(train, "number"))
                            .trainName(getText(train, "name"))
                            .arrival(getText(stop, "arrival"))
                            .departure(getText(stop, "departure"))
                            .expectedArrival(getText(live, "expectedArrivalTime"))
                            .expectedDeparture(getText(live, "expectedDepartureTime"))
                            .delayMinutes(getInt(live, "delayMinutes"))
                            .platform(getText(live, "platform"))
                            .status(getText(live, "type"))
                            .build();

                    String liveType = row.getStatus();
                    if ("at-station".equals(liveType) || "upcoming".equals(liveType)) {
                        arriving.add(row);
                    }
                    if ("departed".equals(liveType)) {
                        departing.add(row);
                    }
                    if (row.getDelayMinutes() != null && row.getDelayMinutes() > 0) {
                        delayed.add(row);
                    }
                }
            }
        }

        return LiveStationBoardResponse.builder()
                .stationCode(stationCode)
                .arrivingTrains(arriving)
                .departingTrains(departing)
                .delayedTrains(delayed)
                .cancelledTrains(cancelled)
                .build();
    }

    // ---------------------------------------------------------------
    // Shared helpers
    // ---------------------------------------------------------------

    /** Builds a lookup of array elements keyed by a string field, for cross-referencing (e.g. route[] by stationCode). */
    private Map<String, JsonNode> indexByField(JsonNode arrayNode, String keyField) {
        Map<String, JsonNode> index = new HashMap<>();
        if (arrayNode == null || !arrayNode.isArray()) {
            return index;
        }
        for (JsonNode element : arrayNode) {
            String key = getText(element, keyField);
            if (key != null) {
                index.put(key, element);
            }
        }
        return index;
    }

    /** Confirms the response succeeded and has a body; logs and returns null otherwise. */
    private JsonNode validData(RailRadarResponse response, String context) {
        if (response == null || !response.success() || response.data() == null || response.data().isNull()) {
            log.warn("RailRadar {} response was empty or unsuccessful; returning empty mapping.", context);
            return null;
        }
        return response.data();
    }

    private List<String> extractStringArray(JsonNode node, String field) {
        List<String> values = new ArrayList<>();
        if (node == null) {
            return values;
        }
        JsonNode target = node.get(field);
        if (target == null || !target.isArray()) {
            return values;
        }
        Iterator<JsonNode> iterator = target.elements();
        while (iterator.hasNext()) {
            values.add(iterator.next().asText());
        }
        return values;
    }

    private String getText(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asText();
    }

    private Integer getInt(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asInt();
    }

    private Double getDouble(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asDouble();
    }
}
