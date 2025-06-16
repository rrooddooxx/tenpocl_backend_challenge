package com.tenpo.mscalculator.calculation.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalculationPercentageAdjusted {
  private BigDecimal firstAmount;
  private BigDecimal secondAmount;
  private BigDecimal baseAmount;
  private BigDecimal percentage;
  private BigDecimal calculationResultAmount;
}
