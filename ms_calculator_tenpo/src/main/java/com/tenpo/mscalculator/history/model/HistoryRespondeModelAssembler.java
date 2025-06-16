package com.tenpo.mscalculator.history.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.tenpo.mscalculator.history.HistoryController;
import com.tenpo.mscalculator.history.domain.HistoryRecord;
import com.tenpo.mscalculator.history.dto.HistoryResponseDto;
import com.tenpo.mscalculator.history.mapper.HistoryMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class HistoryRespondeModelAssembler
    extends RepresentationModelAssemblerSupport<HistoryRecord, HistoryResponseModel> {

  public HistoryRespondeModelAssembler() {
    super(HistoryController.class, HistoryResponseModel.class);
  }

  @Override
  public HistoryResponseModel toModel(HistoryRecord entity) {
    HistoryResponseDto dto = HistoryMapper.toResponseDto(entity);
    HistoryResponseModel model = new HistoryResponseModel(dto);
    return model;
  }

  @Override
  public CollectionModel<HistoryResponseModel> toCollectionModel(
      Iterable<? extends HistoryRecord> entities) {
    CollectionModel<HistoryResponseModel> collection = super.toCollectionModel(entities);
    collection.add(linkTo(methodOn(HistoryController.class)).withSelfRel());
    return collection;
  }
  
}
