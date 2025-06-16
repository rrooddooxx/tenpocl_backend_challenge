package com.tenpo.ms_percentage_tenpo.percentage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PercentageService {
    public BigDecimal getDynamicPercentage() {
        return BigDecimal.valueOf(0.19);
    }
}
