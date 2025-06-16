package com.tenpo.mscalculator.shared.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class RecordMapper {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private RecordMapper() {}

  public static Map<String, Object> toMapFromJsonString(String jsonString) {
    try {
      return MAPPER.readValue(jsonString, new TypeReference<>() {});
    } catch (Exception ex) {
      throw new RuntimeException("Failed to parse Json string to Map", ex);
    }
  }

  public static Map<String, Object> toMapFromObject(Object object) {
    try {
      return MAPPER.convertValue(object, new TypeReference<>() {});
    } catch (Exception ex) {
      throw new RuntimeException("Failed to parse Object to Map", ex);
    }
  }
}
