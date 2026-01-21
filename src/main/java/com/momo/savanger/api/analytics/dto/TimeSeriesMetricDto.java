package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.transaction.dto.TransactionSumAndCount;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeSeriesMetricDto {

    private final LocalDate date;

    private final TransactionSumAndCount incomes;
    private final TransactionSumAndCount expenses;
}
