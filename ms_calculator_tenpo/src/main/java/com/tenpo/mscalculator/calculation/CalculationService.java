package com.tenpo.mscalculator.calculation;

import com.tenpo.mscalculator.calculation.domain.CalculationPercentageAdjusted;
import com.tenpo.mscalculator.history.HistoryService;
import com.tenpo.mscalculator.percentage.PercentageService;
import com.tenpo.mscalculator.percentage.domain.Percentage;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculationService {

  private final PercentageService percentageService;

  public CalculationPercentageAdjusted calculateWithDynamicPercentage(
      CalculationPercentageAdjusted input) {
    BigDecimal sum = input.getFirstAmount().add(input.getSecondAmount());
    BigDecimal percentage = getDynamicPercentage().percentage();
    BigDecimal result = sum.add(sum.multiply(percentage));

    return CalculationPercentageAdjusted.builder()
        .firstAmount(input.getFirstAmount())
        .secondAmount(input.getSecondAmount())
        .baseAmount(sum)
        .percentage(percentage)
        .calculationResultAmount(result)
        .build();
  }

  private Percentage getDynamicPercentage() {
    return percentageService.getPercentage();
  }
}
