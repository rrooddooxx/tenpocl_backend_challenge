package com.tenpo.mscalculator.infrastructure.web.filters;

import com.tenpo.mscalculator.infrastructure.web.events.RequestRegisterEvent;
import com.tenpo.mscalculator.infrastructure.web.utils.EndpointUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "RequestRegisterFilter")
public class RequestRegisterFilter extends OncePerRequestFilter {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = EndpointUtils.servletEndpointMapper(request);
    return path.startsWith("/history");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException {

    ContentCachingRequestWrapper cachedReq = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper cachedRes = new ContentCachingResponseWrapper(response);

    try {
      filterChain.doFilter(cachedReq, cachedRes);
    } catch (Exception ex) {
      log.error("Error while processing request!! {}", ex.getMessage());
    } finally {
      String requestPayload = new String(cachedReq.getContentAsByteArray(), StandardCharsets.UTF_8);
      String responseBody = new String(cachedRes.getContentAsByteArray(), StandardCharsets.UTF_8);

      applicationEventPublisher.publishEvent(
          new RequestRegisterEvent(
              OffsetDateTime.now(),
              EndpointUtils.servletEndpointMapper(cachedReq),
              requestPayload,
              responseBody,
              HttpStatus.valueOf(cachedRes.getStatus())));

      cachedRes.copyBodyToResponse();
    }
  }
}
