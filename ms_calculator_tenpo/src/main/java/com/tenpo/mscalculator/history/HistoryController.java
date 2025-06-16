package com.tenpo.mscalculator.history;

import com.tenpo.mscalculator.history.docs.GetHistoryOperationDoc;
import com.tenpo.mscalculator.history.domain.HistoryRecord;
import com.tenpo.mscalculator.history.model.HistoryRespondeModelAssembler;
import com.tenpo.mscalculator.history.model.HistoryResponseModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Slf4j(topic = "HistoryController")
@Tag(name = "History", description = "Request history management and retrieval")
public class HistoryController {
  private final HistoryService historyService;
  private final HistoryRespondeModelAssembler historyRespondeModelAssembler;

  @GetMapping
  @GetHistoryOperationDoc
  public ResponseEntity<PagedModel<HistoryResponseModel>> getRequestHistory(
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "4") Integer size,
      @RequestParam(defaultValue = "DESC") String order,
      PagedResourcesAssembler<HistoryRecord> pagedAssembler) {

    Page<HistoryRecord> requestHistory = historyService.getRequestHistory(page, size, order);
    PagedModel<HistoryResponseModel> pagedModel =
        pagedAssembler.toModel(requestHistory, historyRespondeModelAssembler);

    return ResponseEntity.ok(pagedModel);
  }
}
