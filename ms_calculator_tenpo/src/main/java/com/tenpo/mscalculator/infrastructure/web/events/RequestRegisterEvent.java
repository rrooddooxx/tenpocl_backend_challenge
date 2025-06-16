package com.tenpo.mscalculator.infrastructure.web.events;

import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;

public record RequestRegisterEvent(
    OffsetDateTime requestDate,
    String endpoint,
    String requestPayload,
    String responseBody,
    HttpStatus httpStatus) {}
