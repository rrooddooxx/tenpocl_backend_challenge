package com.tenpo.ms_percentage_tenpo.percentage.mapper;

import com.tenpo.ms_percentage_tenpo.percentage.dto.PercentageResponseDto;

import java.math.BigDecimal;

public class PercentageMapper {
    public static PercentageResponseDto toDto(BigDecimal percentage) {
        return new PercentageResponseDto(percentage);
    }
}
