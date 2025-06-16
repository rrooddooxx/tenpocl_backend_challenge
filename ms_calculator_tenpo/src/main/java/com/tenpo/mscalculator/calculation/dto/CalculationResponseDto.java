package com.tenpo.mscalculator.calculation.dto;

import java.math.BigDecimal;

public record CalculationResponseDto(BigDecimal result, BigDecimal percentageApplied) {}
