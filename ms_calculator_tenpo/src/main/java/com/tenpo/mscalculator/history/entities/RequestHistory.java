package com.tenpo.mscalculator.history.entities;

import com.tenpo.mscalculator.infrastructure.database.converters.RequestHistoryTypeConverter;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "request_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer requestId;

  @Column(name = "requested_at")
  private OffsetDateTime requestDate;

  @Column(name = "endpoint")
  private String endpoint;

  @Convert(converter = RequestHistoryTypeConverter.class)
  @Column(name = "response_type")
  private RequestHistoryType responseType;

  @Type(JsonBinaryType.class)
  @Column(name = "call_parameters", columnDefinition = "jsonb")
  private Map<String, Object> callParams;

  @Type(JsonBinaryType.class)
  @Column(name = "response", columnDefinition = "jsonb")
  private Map<String, Object> responseBody;
}
