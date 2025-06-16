package com.tenpo.mscalculator.history.mapper;

import com.tenpo.mscalculator.history.domain.HistoryRecord;
import com.tenpo.mscalculator.history.dto.HistoryResponseDto;
import com.tenpo.mscalculator.history.entities.RequestHistory;
import com.tenpo.mscalculator.history.entities.RequestHistoryType;
import com.tenpo.mscalculator.infrastructure.web.events.RequestRegisterEvent;
import com.tenpo.mscalculator.shared.mappers.RecordMapper;

public class HistoryMapper {

  public static HistoryRecord toDomain(RequestHistory result) {
    return HistoryRecord.builder()
        .id(result.getRequestId())
        .type(result.getResponseType())
        .requestDate(result.getRequestDate())
        .endpoint(result.getEndpoint())
        .callParams(result.getCallParams())
        .responseBody(result.getResponseBody())
        .build();
  }

  public static RequestHistory toEntity(RequestRegisterEvent event) {
    return RequestHistory.builder()
        .requestDate(event.requestDate())
        .endpoint(event.endpoint())
        .callParams(RecordMapper.toMapFromJsonString(event.requestPayload()))
        .responseBody(RecordMapper.toMapFromJsonString(event.responseBody()))
        .responseType(RequestHistoryType.fromHttpStatus(event.httpStatus()))
        .build();
  }

  public static HistoryResponseDto toResponseDto(HistoryRecord result) {
    return new HistoryResponseDto(
        result.getId(),
        result.getRequestDate(),
        result.getEndpoint(),
        result.getType(),
        result.getCallParams(),
        result.getResponseBody());
  }
}
