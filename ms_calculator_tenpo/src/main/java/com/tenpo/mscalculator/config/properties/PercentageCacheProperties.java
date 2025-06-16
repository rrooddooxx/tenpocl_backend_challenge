package com.tenpo.mscalculator.config.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache.percentage-service")
public record PercentageCacheProperties(Duration ttl) {}
