package com.tenpo.mscalculator.history.dto;

import com.tenpo.mscalculator.history.entities.RequestHistoryType;
import java.time.OffsetDateTime;
import java.util.Map;

public record HistoryResponseDto(
    Integer requestId,
    OffsetDateTime requestDate,
    String requestEndpoint,
    RequestHistoryType requestType,
    Map<String, Object> requestParameters,
    Map<String, Object> responseBody
) {}
