package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.transaction.dto.TransactionSumAndCount;

public interface AnalyticDto {

    TransactionSumAndCount getIncomes();

    TransactionSumAndCount getExpenses();
}
