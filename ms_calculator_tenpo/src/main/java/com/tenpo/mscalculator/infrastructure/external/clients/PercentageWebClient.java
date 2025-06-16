package com.tenpo.mscalculator.infrastructure.external.clients;

import com.tenpo.mscalculator.config.properties.PercentageServiceProperties;
import com.tenpo.mscalculator.infrastructure.web.exceptions.PercentageNotAvailableError;
import com.tenpo.mscalculator.percentage.PercentageConstants;
import com.tenpo.mscalculator.percentage.dto.PercentageResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PercentageWebClient")
public class PercentageWebClient {

  private final WebClient webClient;
  private final PercentageServiceProperties properties;
  private final CacheManager cacheManager;

  private PercentageResponseModel getPercentage() {
    return webClient
        .get()
        .uri(properties.baseUrl().concat(properties.endpoints().getPercentage()))
        .retrieve()
        .bodyToMono(PercentageResponseModel.class)
        .timeout(properties.timeout())
        .block();
  }

  @Cacheable(value = PercentageConstants.PERCENTAGE_CACHE_KEY, unless = "#result == null")
  public PercentageResponseModel fetchPercentage() {
    try {
      return getPercentage();
    } catch (Exception ex) {
      return recoverFromCacheOrThrow(ex);
    }
  }

  private PercentageResponseModel recoverFromCacheOrThrow(Exception ex) {
    log.error(
        "Failed request from percentage service. Getting last value from cache! Error: {}",
        ex.getMessage());

    Cache cache = cacheManager.getCache(PercentageConstants.PERCENTAGE_CACHE_KEY);
    var cached = cache != null ? cache.get(SimpleKey.EMPTY, PercentageResponseModel.class) : null;
    if (cached != null) {
      return cached;
    }

    throw new PercentageNotAvailableError("Error retrieving percentage from cache!");
  }
}
