package com.tenpo.mscalculator.calculation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CalculationRequestDto(
    @NotNull(message = "First amount must not be null")
        @DecimalMin(value = "0.01", message = "First amount must be greater than 0.01")
        BigDecimal firstAmount,
    @NotNull(message = "Second amount must not be null")
        @DecimalMin(value = "0.01", message = "Second amount must be greater than 0.01")
        @NotNull(message = "Second amount must not be null")
        BigDecimal secondAmount) {}
