package com.railtrack.common.dto;

import com.fasterxml.jackson.databind.JsonNode;

/** Immutable RailRadar response envelope retaining the complete payload. */
public record RailRadarResponse(boolean success, JsonNode data, JsonNode meta) {
}
