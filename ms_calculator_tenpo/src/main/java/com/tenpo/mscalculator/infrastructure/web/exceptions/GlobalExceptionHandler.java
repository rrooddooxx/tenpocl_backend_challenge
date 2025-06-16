package com.tenpo.mscalculator.infrastructure.web.exceptions;

import com.tenpo.mscalculator.infrastructure.web.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<InvalidArgumentsErrorDto> handleValidationErrors(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    logStackTrace(ex, "MethodArgumentNotValidException");

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String errorCode = ((FieldError) error).getField();
              errors.put(errorCode, error.getDefaultMessage());
            });

    InvalidArgumentsErrorDto responseDto =
        new InvalidArgumentsErrorDto(ErrorCodes.INVALID_CALCULATION_ARGUMENTS, errors);

    return ResponseEntity.badRequest().body(responseDto);
  }

  @ExceptionHandler(PercentageNotAvailableError.class)
  public ResponseEntity<PercentageNotAvailableErrorDto> handlePercentageNotAvailable(
      PercentageNotAvailableError ex) {

    logStackTrace(ex, ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus())
        .body(new PercentageNotAvailableErrorDto(ex.getErrorCode(), ex.getMessage()));
  }

  @ExceptionHandler(RequestHistoryNotAvailableError.class)
  public ResponseEntity<RequestHistoryNotAvailableErrorDto> handleRequestHistoryNotAvailable(
      RequestHistoryNotAvailableError ex) {
    logStackTrace(ex, ex.getMessage());
    return ResponseEntity.status(ex.getHttpStatus())
        .body(new RequestHistoryNotAvailableErrorDto(ex.getErrorCode(), ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GenericErrorDto> handleGlobalException(Exception ex) {

    logStackTrace(ex, "Generic Exception");
    return ResponseEntity.internalServerError()
        .body(new GenericErrorDto(ErrorCodes.GENERIC_ERROR, ex.getMessage()));
  }

  private void logStackTrace(Exception ex, String message) {
    if (ex == null) {
      return;
    }

    StackTraceElement[] trace = ex.getStackTrace();
    if (trace != null && trace.length > 1) {
      log.error("Exception ({}) triggered from caller: {}", message, trace[0], ex.getCause());
    }
  }
}
