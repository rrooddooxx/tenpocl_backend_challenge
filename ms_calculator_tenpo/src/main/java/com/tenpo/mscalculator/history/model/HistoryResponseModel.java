package com.tenpo.mscalculator.history.model;

import com.tenpo.mscalculator.history.dto.HistoryResponseDto;
import com.tenpo.mscalculator.history.entities.RequestHistoryType;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Builder
@RequiredArgsConstructor
@Getter
@Relation(itemRelation = "record", collectionRelation = "records")
public class HistoryResponseModel extends RepresentationModel<HistoryResponseModel> {
  private final Integer requestId;
  private final OffsetDateTime requestDate;
  private final String requestEndpoint;
  private final RequestHistoryType requestType;
  private final Map<String, Object> requestParameters;
  private final Map<String, Object> responseBody;

  public HistoryResponseModel(HistoryResponseDto data) {
    this.requestId = data.requestId();
    this.requestDate = data.requestDate();
    this.requestEndpoint = data.requestEndpoint();
    this.requestType = data.requestType();
    this.requestParameters = data.requestParameters();
    this.responseBody = data.responseBody();
  }
}
