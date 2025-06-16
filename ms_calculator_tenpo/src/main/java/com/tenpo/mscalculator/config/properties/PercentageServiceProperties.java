package com.tenpo.mscalculator.config.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.percentage-service")
public record PercentageServiceProperties(String baseUrl, Endpoints endpoints, Duration timeout) {
  public record Endpoints(String getPercentage, String healthCheck) {}
}
