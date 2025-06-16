package com.tenpo.mscalculator.calculation;

import com.tenpo.mscalculator.calculation.docs.CalculateOperationDoc;
import com.tenpo.mscalculator.calculation.domain.CalculationPercentageAdjusted;
import com.tenpo.mscalculator.calculation.dto.CalculationRequestDto;
import com.tenpo.mscalculator.calculation.dto.CalculationResponseDto;
import com.tenpo.mscalculator.calculation.mapper.CalculationMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculation")
@RequiredArgsConstructor
@Slf4j(topic = "CalculationController")
@Tag(name = "Calculation", description = "Realiza cálculo de valores y aplica porcentaje adicional.")
public class CalculationController {

  private final CalculationService calculationService;

  @PostMapping("/calculate")
  @CalculateOperationDoc
  public ResponseEntity<CalculationResponseDto> getCalculation(
      @Valid @RequestBody CalculationRequestDto requestDto) {

    CalculationPercentageAdjusted response =
        calculationService.calculateWithDynamicPercentage(
            CalculationMapper.mapDtoToModel(requestDto));

    CalculationResponseDto responseDto = CalculationMapper.mapModelToResponseDto(response);
    return ResponseEntity.ok().body(responseDto);
  }
}
