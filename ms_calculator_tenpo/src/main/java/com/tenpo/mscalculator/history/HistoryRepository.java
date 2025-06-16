package com.tenpo.mscalculator.history;

import com.tenpo.mscalculator.history.entities.RequestHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface HistoryRepository
    extends PagingAndSortingRepository<RequestHistory, Integer>,
        CrudRepository<RequestHistory, Integer> {}
