package com.tenpo.mscalculator.infrastructure.database.converters;

import com.tenpo.mscalculator.history.entities.RequestHistoryType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

@Converter(autoApply = true)
public class RequestHistoryTypeConverter
    implements AttributeConverter<RequestHistoryType, Integer> {

  @Override
  public Integer convertToDatabaseColumn(RequestHistoryType attribute) {
    return attribute == null ? null : attribute.getRequestId();
  }

  @Override
  public RequestHistoryType convertToEntityAttribute(Integer dbData) {
    if (dbData == null) return null;

    return Arrays.stream(RequestHistoryType.values())
        .filter(val -> val.getRequestId().equals(dbData))
        .findFirst()
        .orElse(RequestHistoryType.UNKNOWN);
  }
}
