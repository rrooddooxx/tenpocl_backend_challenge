package com.tenpo.mscalculator.calculation.docs;

import com.tenpo.mscalculator.calculation.dto.CalculationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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
    summary = "Realiza cálculo de valores y aplica porcentaje adicional.",
    description =
        "Realiza el cálculo de la suma de los dos valores entregados y les aplica porcentaje adicional, de acuerdo a "
            + "valor obtenido de microservicio de porcentajes. Cuenta con un caché de 30 minutos.",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "Cálculo realizado exitosamente.",
          content = @Content(schema = @Schema(implementation = CalculationResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Parámetros inválidos en la solicitud"),
      @ApiResponse(
          responseCode = "503",
          description =
              "Error interno del servidor: No se pudo contactar al servicio "
                  + "externo de porcentajes y el valor de la caché está "
                  + "expirado.")
    })
public @interface CalculateOperationDoc {}
