package com.tenpo.mscalculator.history.docs;

import com.tenpo.mscalculator.history.dto.HistoryResponseDto;
import com.tenpo.mscalculator.history.model.HistoryResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(
    summary = "Obtener Historial de Solicitudes. Entrega resultado paginado.",
    description =
        "Historial paginado de todas las solicitudes al servicio de cálculos con porcentaje.",
    parameters = {
      @Parameter(
          name = "page",
          description = "Número de página (comienza desde 0)",
          example = "0",
          schema = @Schema(type = "integer", minimum = "0")),
      @Parameter(
          name = "size",
          description = "Número de registros por página",
          example = "4",
          schema = @Schema(type = "integer", minimum = "1", maximum = "100")),
      @Parameter(
          name = "order",
          description = "Orden de los resultados (asc = ascendente, desc = descendente)",
          example = "desc",
          schema =
              @Schema(
                  type = "string",
                  allowableValues = {"asc", "desc"}))
    },
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "Historial obtenido exitosamente",
          content = @Content(schema = @Schema(implementation = HistoryResponseModel.class))),
      @ApiResponse(
          responseCode = "500",
          description =
              "Error interno del servidor al obtener el historial. Puede ser"
                  + " por pérdida de conexión con la base de datos")
    })
public @interface GetHistoryOperationDoc {}
