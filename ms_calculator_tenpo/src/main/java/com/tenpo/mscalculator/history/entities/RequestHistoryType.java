package com.tenpo.mscalculator.history.entities;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Slf4j
public enum RequestHistoryType {
  SUCCESS(1),
  ERROR(2),
  UNKNOWN(3);

  private final Integer requestId;

  RequestHistoryType(Integer requestId) {
    this.requestId = requestId;
  }

  public static RequestHistoryType fromHttpStatus(HttpStatus httpStatus) {
    try {
      if (httpStatus.is2xxSuccessful()) {
        return RequestHistoryType.SUCCESS;
      }
      return RequestHistoryType.ERROR;
    } catch (Exception ex) {
      log.error("Error mapping http status to RequestHistoryType: {}", ex.getMessage());
      return RequestHistoryType.UNKNOWN;
    }
  }
}
