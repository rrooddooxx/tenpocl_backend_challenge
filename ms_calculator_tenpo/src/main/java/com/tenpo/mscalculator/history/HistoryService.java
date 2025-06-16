package com.tenpo.mscalculator.history;

import com.tenpo.mscalculator.history.domain.HistoryRecord;
import com.tenpo.mscalculator.history.entities.RequestHistory;
import com.tenpo.mscalculator.history.mapper.HistoryMapper;
import com.tenpo.mscalculator.infrastructure.web.events.RequestRegisterEvent;
import com.tenpo.mscalculator.infrastructure.web.exceptions.RequestHistoryNotAvailableError;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryService {
  public final HistoryRepository historyRepository;

  private Optional<Page<RequestHistory>> fetchAllRequestHistory(Pageable pageable) {
    try {
      Page<RequestHistory> results = historyRepository.findAll(pageable);
      return Optional.of(results);
    } catch (Exception e) {
      log.error("Error while fetching request history: {}", e.getMessage());
      return Optional.empty();
    }
  }

  public Page<HistoryRecord> getRequestHistory(Integer page, Integer size, String order) {
    Sort sort = Sort.by(Sort.Direction.fromString(order), "requestDate");
    Pageable pageable = PageRequest.of(page, size, sort);
    Optional<Page<RequestHistory>> history = fetchAllRequestHistory(pageable);
    if (history.isPresent()) {
      return history.get().map(HistoryMapper::toDomain);
    }

    throw new RequestHistoryNotAvailableError();
  }

  @Async
  @EventListener
  public void saveRequestHistory(RequestRegisterEvent requestEvent) {
    try {
      log.info("Saving request history for endpoint: {}", requestEvent.endpoint());
      historyRepository.save(HistoryMapper.toEntity(requestEvent));
    } catch (Exception e) {
      log.error("Error while saving request history: {}", e.getMessage());
    }
  }
}
