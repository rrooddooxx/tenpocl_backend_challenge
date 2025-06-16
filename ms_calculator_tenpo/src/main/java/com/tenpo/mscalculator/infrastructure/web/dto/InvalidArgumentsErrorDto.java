package com.tenpo.mscalculator.infrastructure.web.dto;

import java.util.Map;

public record InvalidArgumentsErrorDto(ErrorCodes errorCode, Map<String, String> errors) {}
