package com.tenpo.mscalculator.infrastructure.web.exceptions;

import com.tenpo.mscalculator.infrastructure.web.dto.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PercentageNotAvailableError extends RuntimeException {
  public final ErrorCodes errorCode = ErrorCodes.PERCENTAGE_NOT_AVAILABLE;
  public final HttpStatus httpStatus = HttpStatus.CONFLICT;
  public String message;

  public PercentageNotAvailableError(String msg) {
    super(msg);
    this.message = msg;
  }
}
