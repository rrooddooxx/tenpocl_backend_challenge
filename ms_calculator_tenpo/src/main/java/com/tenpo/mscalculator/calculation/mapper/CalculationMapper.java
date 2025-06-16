package com.tenpo.mscalculator.calculation.mapper;

import com.tenpo.mscalculator.calculation.domain.CalculationPercentageAdjusted;
import com.tenpo.mscalculator.calculation.dto.CalculationRequestDto;
import com.tenpo.mscalculator.calculation.dto.CalculationResponseDto;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CalculationMapper {
  public static CalculationPercentageAdjusted mapDtoToModel(CalculationRequestDto requestDto) {
    return CalculationPercentageAdjusted.builder()
        .firstAmount(requestDto.firstAmount())
        .secondAmount(requestDto.secondAmount())
        .baseAmount(BigDecimal.ZERO)
        .percentage(BigDecimal.ZERO)
        .calculationResultAmount(BigDecimal.ZERO)
        .build();
  }

  public static CalculationResponseDto mapModelToResponseDto(CalculationPercentageAdjusted result) {
    return new CalculationResponseDto(result.getCalculationResultAmount(), result.getPercentage());
  }
}
