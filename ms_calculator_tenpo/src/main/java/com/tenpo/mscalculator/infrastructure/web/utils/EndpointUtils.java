package com.tenpo.mscalculator.infrastructure.web.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EndpointUtils {
  public static String servletEndpointMapper(HttpServletRequest req) {
    try {
      if (req.getContextPath() != null && !req.getContextPath().isEmpty()) {
        return req.getRequestURI().substring(req.getContextPath().length());
      }
      return req.getRequestURI();
    } catch (Exception e) {
      if (req.getRequestURI() != null && req.getRequestURI().length() > 1) {
        return req.getRequestURI().substring(1);
      }
      return "NOT_AVAILABLE";
    }
  }
}
