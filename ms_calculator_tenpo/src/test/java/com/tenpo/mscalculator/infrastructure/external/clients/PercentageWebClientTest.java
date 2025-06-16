package com.tenpo.mscalculator.infrastructure.external.clients;

import static com.tenpo.mscalculator.percentage.PercentageConstants.PERCENTAGE_CACHE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.tenpo.mscalculator.config.properties.PercentageServiceProperties;
import com.tenpo.mscalculator.infrastructure.web.exceptions.PercentageNotAvailableError;
import com.tenpo.mscalculator.percentage.PercentageConstants;
import com.tenpo.mscalculator.percentage.dto.PercentageResponseModel;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("[WEB CLIENT] PercentageWebClient")
class PercentageWebClientTest {

  @Mock private WebClient webClient;
  @Mock private PercentageServiceProperties properties;
  @Mock private CacheManager cacheManager;
  @Mock private Cache cache;
  @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock private WebClient.ResponseSpec responseSpec;
  @Mock private PercentageServiceProperties.Endpoints endpoints;
  @InjectMocks private PercentageWebClient percentageWebClient;

  private final BigDecimal mockPercentage = new BigDecimal("15.50");
  private final String baseUrl = "https://api.mock.com";
  private final String endpoint = "/percentage";
  private final Duration timeout = Duration.ofSeconds(5);

  @BeforeEach
  void setUp() {
    when(properties.baseUrl()).thenReturn(baseUrl);
    when(properties.endpoints()).thenReturn(endpoints);
    when(endpoints.getPercentage()).thenReturn(endpoint);
    when(properties.timeout()).thenReturn(timeout);
  }

  @Test
  @DisplayName("Should return percentage response when external service succeeds")
  void shouldReturnPercentageResponseWhenExternalServiceSucceeds() {
    PercentageResponseModel expectedResponse = new PercentageResponseModel(mockPercentage);
    setupSuccessfulWebClientCall(expectedResponse);

    PercentageResponseModel result = percentageWebClient.fetchPercentage();

    assertThat(result).isNotNull();
    assertThat(result.percentage()).isEqualTo(mockPercentage);
    verify(webClient).get();
    verify(requestHeadersUriSpec).uri(baseUrl + endpoint);
    verify(responseSpec).bodyToMono(PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should get value from cache when web client throws request exception")
  void shouldGetFromCacheWhenWebClientThrowsRequestException() {
    PercentageResponseModel cachedResponse = new PercentageResponseModel(mockPercentage);
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(
        new WebClientRequestException(new RuntimeException("failed"), null, null, HttpHeaders.EMPTY));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenReturn(cachedResponse);

    PercentageResponseModel result = percentageWebClient.fetchPercentage();

    assertThat(result).isNotNull();
    assertThat(result.percentage()).isEqualTo(mockPercentage);
    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should get value from cache when web client throws response exception")
  void shouldGetFromCacheWhenWebClientThrowsResponseException() {
    PercentageResponseModel cachedResponse = new PercentageResponseModel(mockPercentage);
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(
        WebClientResponseException.create(500, "Internal Server Error", null, null, null));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenReturn(cachedResponse);

    PercentageResponseModel result = percentageWebClient.fetchPercentage();

    assertThat(result).isNotNull();
    assertThat(result.percentage()).isEqualTo(mockPercentage);
    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should get value from cache when request timeout occurs")
  void shouldGetFromCacheWhenRequestTimeoutOccurs() {
    PercentageResponseModel cachedResponse = new PercentageResponseModel(mockPercentage);
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(new TimeoutException("timeout"));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenReturn(cachedResponse);

    PercentageResponseModel result = percentageWebClient.fetchPercentage();

    assertThat(result).isNotNull();
    assertThat(result.percentage()).isEqualTo(mockPercentage);
    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should throw error when external service fails and cache is empty")
  void shouldThrowErrorWhenExternalServiceFailsAndCacheIsEmpty() {
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(
        new WebClientRequestException(new RuntimeException("Connection failed"), null, null, HttpHeaders.EMPTY));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenReturn(null);

    assertThatThrownBy(() -> percentageWebClient.fetchPercentage())
        .isInstanceOf(PercentageNotAvailableError.class);

    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should throw error when external service fails and cache contains null")
  void shouldThrowErrorWhenExternalServiceFailsAndCacheContainsNull() {
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(
        new WebClientRequestException(new RuntimeException("failed"), null, null, HttpHeaders.EMPTY));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenReturn(null);

    assertThatThrownBy(() -> percentageWebClient.fetchPercentage())
        .isInstanceOf(PercentageNotAvailableError.class);

    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should throw error when cache manager returns null")
  void shouldThrowErrorWhenCacheManagerReturnsNull() {
    when(cacheManager.getCache(PercentageConstants.PERCENTAGE_CACHE_KEY)).thenReturn(null);
    setupFailingWebClientCall(
        new WebClientRequestException(new RuntimeException("failed"), null, null, HttpHeaders.EMPTY));

    assertThatThrownBy(() -> percentageWebClient.fetchPercentage())
        .isInstanceOf(PercentageNotAvailableError.class);

    verify(cacheManager).getCache(PercentageConstants.PERCENTAGE_CACHE_KEY);
  }

  @Test
  @DisplayName("Should get value from cache when unexpected exception occurs")
  void shouldGetFromCacheWhenUnexpectedExceptionOccurs() {
    PercentageResponseModel cachedResponse = new PercentageResponseModel(mockPercentage);
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(new RuntimeException("Unexpected error"));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenReturn(cachedResponse);

    PercentageResponseModel result = percentageWebClient.fetchPercentage();

    assertThat(result).isNotNull();
    assertThat(result.percentage()).isEqualTo(mockPercentage);
    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should throw error when unexpected exception occurs and cache is empty")
  void shouldThrowErrorWhenUnexpectedExceptionOccursAndCacheIsEmpty() {
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(new RuntimeException("Unexpected error"));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenReturn(null);

    assertThatThrownBy(() -> percentageWebClient.fetchPercentage())
        .isInstanceOf(PercentageNotAvailableError.class);

    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  @Test
  @DisplayName("Should construct correct path for external service call")
  void shouldConstructCorrectPathForExternalServiceCall() {
    PercentageResponseModel expectedResponse = new PercentageResponseModel(mockPercentage);
    setupSuccessfulWebClientCall(expectedResponse);

    percentageWebClient.fetchPercentage();

    verify(requestHeadersUriSpec).uri(baseUrl + endpoint);
  }

  @Test
  @DisplayName("Should propagate exception when cache retrieval fails")
  void shouldPropagateExceptionWhenCacheRetrievalFails() {
    when(cacheManager.getCache(PERCENTAGE_CACHE_KEY)).thenReturn(cache);
    setupFailingWebClientCall(
        new WebClientRequestException(new RuntimeException("Connection failed"), null, null, HttpHeaders.EMPTY));
    when(cache.get(SimpleKey.EMPTY, PercentageResponseModel.class)).thenThrow(new RuntimeException("Cache error"));

    assertThatThrownBy(() -> percentageWebClient.fetchPercentage())
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Cache error");

    verify(cache).get(SimpleKey.EMPTY, PercentageResponseModel.class);
  }

  private void setupSuccessfulWebClientCall(PercentageResponseModel response) {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PercentageResponseModel.class))
        .thenReturn(Mono.justOrEmpty(response).timeout(timeout));
  }

  private void setupFailingWebClientCall(Exception exception) {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PercentageResponseModel.class)).thenReturn(Mono.<PercentageResponseModel>error(exception).timeout(timeout));
  }
}
