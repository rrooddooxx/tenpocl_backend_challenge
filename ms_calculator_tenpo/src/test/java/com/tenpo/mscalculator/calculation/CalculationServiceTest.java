package com.tenpo.mscalculator.calculation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tenpo.mscalculator.calculation.domain.CalculationPercentageAdjusted;
import com.tenpo.mscalculator.history.HistoryService;
import com.tenpo.mscalculator.infrastructure.web.exceptions.PercentageNotAvailableError;
import com.tenpo.mscalculator.percentage.PercentageService;
import com.tenpo.mscalculator.percentage.domain.Percentage;
import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[SERVICE] CalculationService")
class CalculationServiceTest {

  @Mock private PercentageService percentageService;
//  @Mock private HistoryService historyService;
  @InjectMocks private CalculationService calculationService;

  @Test
  @DisplayName("Should calculate correctly with valid input amounts")
  void shouldCalculateCorrectlyWithValidInputAmounts() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("10.00"))
        .secondAmount(new BigDecimal("20.00"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.10"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    assertThat(result.getFirstAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(result.getSecondAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
    assertThat(result.getBaseAmount()).isEqualByComparingTo(new BigDecimal("30.00"));
    assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("0.10"));
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(new BigDecimal("33.00"));
    verify(percentageService).getPercentage();
  }

  @Test
  @DisplayName("Should return sum only when percentage is zero")
  void shouldReturnSumOnlyWhenPercentageIsZero() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("15.50"))
        .secondAmount(new BigDecimal("24.50"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.00"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    assertThat(result.getBaseAmount()).isEqualByComparingTo(new BigDecimal("40.00"));
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(new BigDecimal("40.00"));
    assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("0.00"));
  }

  @Test
  @DisplayName("Should calculate correctly with high percentage values")
  void shouldCalculateCorrectlyWithHighPercentageValues() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("100.00"))
        .secondAmount(new BigDecimal("200.00"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.50"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    assertThat(result.getBaseAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(new BigDecimal("450.00"));
    assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("0.50"));
  }

  @Test
  @DisplayName("Should handle precision correctly with decimal amounts")
  void shouldHandlePrecisionCorrectlyWithDecimalAmounts() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("12.345"))
        .secondAmount(new BigDecimal("23.456"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.15"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    BigDecimal expectedBase = new BigDecimal("35.801");
    BigDecimal expectedResult = expectedBase.add(expectedBase.multiply(new BigDecimal("0.15")));

    assertThat(result.getBaseAmount()).isEqualByComparingTo(expectedBase);
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(expectedResult);
  }

  @Test
  @DisplayName("Should reduce total when percentage is negative")
  void shouldReduceTotalWhenPercentageIsNegative() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("50.00"))
        .secondAmount(new BigDecimal("30.00"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("-0.20"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    assertThat(result.getBaseAmount()).isEqualByComparingTo(new BigDecimal("80.00"));
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(new BigDecimal("64.00"));
    assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("-0.20"));
  }

  @Test
  @DisplayName("Should maintain precision with very small amounts")
  void shouldMaintainPrecisionWithVerySmallAmounts() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("0.01"))
        .secondAmount(new BigDecimal("0.02"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.05"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    BigDecimal expectedBase = new BigDecimal("0.03");
    BigDecimal expectedResult = expectedBase.add(expectedBase.multiply(new BigDecimal("0.05")));

    assertThat(result.getBaseAmount()).isEqualByComparingTo(expectedBase);
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(expectedResult);
  }

  @Test
  @DisplayName("Should handle large amounts correctly")
  void shouldHandleLargeAmountsCorrectly() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("999999.99"))
        .secondAmount(new BigDecimal("888888.88"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.025"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    BigDecimal expectedBase = new BigDecimal("1888888.87");
    BigDecimal expectedResult = expectedBase.add(expectedBase.multiply(new BigDecimal("0.025")));

    assertThat(result.getBaseAmount()).isEqualByComparingTo(expectedBase);
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(expectedResult);
  }

  @Test
  @DisplayName("Should propagate exception when percentage service fails")
  void shouldPropagateExceptionWhenPercentageServiceFails() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("10.00"))
        .secondAmount(new BigDecimal("20.00"))
        .build();

    when(percentageService.getPercentage())
        .thenThrow(new PercentageNotAvailableError("Service unavailable"));

    assertThatThrownBy(() -> calculationService.calculateWithDynamicPercentage(input))
        .isInstanceOf(PercentageNotAvailableError.class)
        .hasMessage("Service unavailable");

    verify(percentageService).getPercentage();
  }

  @Test
  @DisplayName("Should propagate runtime exception from percentage service")
  void shouldPropagateRuntimeExceptionFromPercentageService() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("5.00"))
        .secondAmount(new BigDecimal("15.00"))
        .build();

    when(percentageService.getPercentage())
        .thenThrow(new RuntimeException("Unexpected error"));

    assertThatThrownBy(() -> calculationService.calculateWithDynamicPercentage(input))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Unexpected error");
  }

  @Test
  @DisplayName("Should ignore existing calculated fields and recalculate")
  void shouldIgnoreExistingCalculatedFieldsAndRecalculate() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("10.00"))
        .secondAmount(new BigDecimal("20.00"))
        .baseAmount(new BigDecimal("999.99"))
        .percentage(new BigDecimal("0.99"))
        .calculationResultAmount(new BigDecimal("888.88"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.10"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    assertThat(result.getBaseAmount()).isEqualByComparingTo(new BigDecimal("30.00"));
    assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("0.10"));
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(new BigDecimal("33.00"));
  }

  @Test
  @DisplayName("Should maintain accuracy with high precision percentages")
  void shouldMaintainAccuracyWithHighPrecisionPercentages() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("100.00"))
        .secondAmount(new BigDecimal("200.00"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.123456789"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    BigDecimal expectedBase = new BigDecimal("300.00");
    BigDecimal percentageMultiplier = new BigDecimal("0.123456789");
    BigDecimal expectedResult = expectedBase.add(expectedBase.multiply(percentageMultiplier));

    assertThat(result.getBaseAmount()).isEqualByComparingTo(expectedBase);
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(expectedResult);
    assertThat(result.getPercentage()).isEqualByComparingTo(percentageMultiplier);
  }

  @Test
  @DisplayName("Should always call percentage service for fresh data")
  void shouldAlwaysCallPercentageServiceForFreshData() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("1.00"))
        .secondAmount(new BigDecimal("2.00"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("0.05"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    calculationService.calculateWithDynamicPercentage(input);
    calculationService.calculateWithDynamicPercentage(input);

    verify(percentageService, org.mockito.Mockito.times(2)).getPercentage();
  }

  @Test
  @DisplayName("Should double amount when percentage is exactly one hundred percent")
  void shouldDoubleAmountWhenPercentageIsExactlyOneHundredPercent() {
    CalculationPercentageAdjusted input = CalculationPercentageAdjusted.builder()
        .firstAmount(new BigDecimal("25.00"))
        .secondAmount(new BigDecimal("75.00"))
        .build();

    Percentage mockPercentage = new Percentage(new BigDecimal("1.00"));
    when(percentageService.getPercentage()).thenReturn(mockPercentage);

    CalculationPercentageAdjusted result = calculationService.calculateWithDynamicPercentage(input);

    assertThat(result.getBaseAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
    assertThat(result.getCalculationResultAmount()).isEqualByComparingTo(new BigDecimal("200.00"));
    assertThat(result.getPercentage()).isEqualByComparingTo(new BigDecimal("1.00"));
  }
}
