package com.tenpo.mscalculator.history.domain;

import com.tenpo.mscalculator.history.entities.RequestHistoryType;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HistoryRecord {
  private final Integer id;
  private final OffsetDateTime requestDate;
  private final String endpoint;
  private final RequestHistoryType type;
  private final Map<String, Object> callParams;
  private final Map<String, Object> responseBody;
}
