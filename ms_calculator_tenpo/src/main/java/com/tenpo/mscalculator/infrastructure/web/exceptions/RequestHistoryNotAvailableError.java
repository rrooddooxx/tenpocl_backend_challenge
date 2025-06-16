package com.tenpo.mscalculator.infrastructure.web.exceptions;

import com.tenpo.mscalculator.infrastructure.web.dto.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RequestHistoryNotAvailableError extends RuntimeException {
  public HttpStatus httpStatus;
  public ErrorCodes errorCode;

  public RequestHistoryNotAvailableError() {
    super("Request History not available!");
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    this.errorCode = ErrorCodes.REQUEST_HISTORY_NOT_AVAILABLE;
  }
}
