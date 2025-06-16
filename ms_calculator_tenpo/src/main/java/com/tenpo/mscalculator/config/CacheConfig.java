package com.tenpo.mscalculator.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.tenpo.mscalculator.config.properties.PercentageCacheProperties;
import com.tenpo.mscalculator.percentage.PercentageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CacheConfig {

  private final PercentageCacheProperties properties;

  @Bean
  public CacheManager cacheManager() {
    long ttlDuration = properties.ttl().toMinutes();
    String min = ttlDuration > 1L ? "minutes" : "minute";
    log.info("Configured Caffeine Cache with TTL of {} {}!", ttlDuration, min);
    Caffeine<Object, Object> caffeine = Caffeine.newBuilder().expireAfterWrite(properties.ttl());

    CaffeineCacheManager cacheManager =
        new CaffeineCacheManager(PercentageConstants.PERCENTAGE_CACHE_KEY);
    cacheManager.setCaffeine(caffeine);
    return cacheManager;
  }
}
