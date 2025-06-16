package com.tenpo.mscalculator.infrastructure.web.dto;

import lombok.Getter;

@Getter
public enum ErrorCodes {
  INVALID_CALCULATION_ARGUMENTS("INVALID_CALCULATION_ARGUMENTS"),
  PERCENTAGE_NOT_AVAILABLE("PERCENTAGE_NOT_AVAILABLE"),
  REQUEST_HISTORY_NOT_AVAILABLE("REQUEST_HISTORY_NOT_AVAILABLE"),
  GENERIC_ERROR("GENERIC_ERROR");

  private final String errorCode;

  ErrorCodes(String value) {
    this.errorCode = value;
  }
}
