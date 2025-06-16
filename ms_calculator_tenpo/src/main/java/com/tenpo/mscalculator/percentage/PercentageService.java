package com.tenpo.mscalculator.percentage;

import com.tenpo.mscalculator.infrastructure.external.clients.PercentageWebClient;
import com.tenpo.mscalculator.infrastructure.web.exceptions.PercentageNotAvailableError;
import com.tenpo.mscalculator.percentage.domain.Percentage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageService {
  private final PercentageWebClient percentageWebClient;

  public Percentage getPercentage() {
    try {
      return new Percentage(percentageWebClient.fetchPercentage().percentage());
    } catch (Exception ex) {
      log.error("Error getting percentage: {}", ex.getMessage());
      throw new PercentageNotAvailableError(ex.getMessage());
    }
  }
}
