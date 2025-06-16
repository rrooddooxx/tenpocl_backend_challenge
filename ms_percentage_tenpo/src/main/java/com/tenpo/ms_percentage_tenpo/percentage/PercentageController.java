package com.tenpo.ms_percentage_tenpo.percentage;

import com.tenpo.ms_percentage_tenpo.percentage.dto.PercentageResponseDto;
import com.tenpo.ms_percentage_tenpo.percentage.mapper.PercentageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/percentage")
@RequiredArgsConstructor
public class PercentageController {

  private final PercentageService percentageService;

  @GetMapping
  public ResponseEntity<PercentageResponseDto> getPercentage() {
    try {
      PercentageResponseDto response =
          PercentageMapper.toDto(percentageService.getDynamicPercentage());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
