package com.momo.savanger.api.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeSeriesMetricDto {
    private final LocalDate date;

    private final BigDecimal totalIncomes;
    private final BigDecimal totalExpenses;
    private final Long totalTransactions;
}
