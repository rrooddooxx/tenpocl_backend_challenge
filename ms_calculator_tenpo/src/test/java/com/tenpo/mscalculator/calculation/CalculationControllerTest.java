package com.tenpo.mscalculator.calculation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.mscalculator.calculation.domain.CalculationPercentageAdjusted;
import com.tenpo.mscalculator.calculation.dto.CalculationRequestDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CalculationController.class)
@DisplayName("[CONTROLLER] CalculationController")
class CalculationControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private CalculationService calculationService;

  @Test
  @DisplayName("Should calculate successfully with valid input")
  void shouldCalculateSuccessfullyWithValidInput() throws Exception {
    CalculationRequestDto request =
        new CalculationRequestDto(new BigDecimal("10.00"), new BigDecimal("20.00"));

    CalculationPercentageAdjusted serviceResponse =
        CalculationPercentageAdjusted.builder()
            .firstAmount(new BigDecimal("10.00"))
            .secondAmount(new BigDecimal("20.00"))
            .baseAmount(new BigDecimal("30.00"))
            .percentage(new BigDecimal("0.10"))
            .calculationResultAmount(new BigDecimal("33.00"))
            .build();

    when(calculationService.calculateWithDynamicPercentage(
            any(CalculationPercentageAdjusted.class)))
        .thenReturn(serviceResponse);

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.result").value(33.00))
        .andExpect(jsonPath("$.percentageApplied").value(0.10));
  }

  @Test
  @DisplayName("Should return 400 when first amount is null")
  void shouldReturn400WhenFirstAmountIsNull() throws Exception {
    CalculationRequestDto request = new CalculationRequestDto(null, new BigDecimal("20.00"));

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 status when second amount is null")
  void shouldReturn400WhenSecondAmountIsNull() throws Exception {
    CalculationRequestDto request = new CalculationRequestDto(new BigDecimal("10.00"), null);

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 status when first amount is less than minimum")
  void shouldReturn400WhenFirstAmountIsLessThanMinimum() throws Exception {
    CalculationRequestDto request =
        new CalculationRequestDto(new BigDecimal("0.00"), new BigDecimal("20.00"));

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 status when second amount is less than minimum")
  void shouldReturn400WhenSecondAmountIsLessThanMinimum() throws Exception {
    CalculationRequestDto request =
        new CalculationRequestDto(new BigDecimal("10.00"), new BigDecimal("0.00"));

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 status when request body is empty")
  void shouldReturn400WhenRequestBodyIsEmpty() throws Exception {
    mockMvc
        .perform(
            post("/calculation/calculate").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 500 when request body is malformed")
  void shouldReturn500WhenRequestBodyIsMalformed() throws Exception {
    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("Should handle large decimal numbers correctly")
  void shouldHandleLargeDecimalNumbersCorrectly() throws Exception {
    CalculationRequestDto request =
        new CalculationRequestDto(new BigDecimal("999999.99"), new BigDecimal("999999.99"));

    CalculationPercentageAdjusted serviceResponse =
        CalculationPercentageAdjusted.builder()
            .firstAmount(new BigDecimal("999999.99"))
            .secondAmount(new BigDecimal("999999.99"))
            .baseAmount(new BigDecimal("1999999.98"))
            .percentage(new BigDecimal("0.05"))
            .calculationResultAmount(new BigDecimal("2099999.9790"))
            .build();

    when(calculationService.calculateWithDynamicPercentage(
            any(CalculationPercentageAdjusted.class)))
        .thenReturn(serviceResponse);

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.result").value(2099999.979))
        .andExpect(jsonPath("$.percentageApplied").value(0.05));
  }

  @Test
  @DisplayName("Should handle minimum valid values")
  void shouldHandleMinimumValidValues() throws Exception {
    CalculationRequestDto request =
        new CalculationRequestDto(new BigDecimal("0.01"), new BigDecimal("0.01"));

    CalculationPercentageAdjusted serviceResponse =
        CalculationPercentageAdjusted.builder()
            .firstAmount(new BigDecimal("0.01"))
            .secondAmount(new BigDecimal("0.01"))
            .baseAmount(new BigDecimal("0.02"))
            .percentage(new BigDecimal("0.15"))
            .calculationResultAmount(new BigDecimal("0.023"))
            .build();

    when(calculationService.calculateWithDynamicPercentage(
            any(CalculationPercentageAdjusted.class)))
        .thenReturn(serviceResponse);

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.result").value(0.023))
        .andExpect(jsonPath("$.percentageApplied").value(0.15));
  }

  @Test
  @DisplayName("Should verify service method is called with correct parameters")
  void shouldVerifyServiceMethodIsCalledWithCorrectParameters() throws Exception {
    CalculationRequestDto request =
        new CalculationRequestDto(new BigDecimal("15.50"), new BigDecimal("25.75"));

    CalculationPercentageAdjusted serviceResponse =
        CalculationPercentageAdjusted.builder()
            .firstAmount(new BigDecimal("15.50"))
            .secondAmount(new BigDecimal("25.75"))
            .baseAmount(new BigDecimal("41.25"))
            .percentage(new BigDecimal("0.08"))
            .calculationResultAmount(new BigDecimal("44.55"))
            .build();

    when(calculationService.calculateWithDynamicPercentage(
            any(CalculationPercentageAdjusted.class)))
        .thenReturn(serviceResponse);

    mockMvc
        .perform(
            post("/calculation/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}
