package com.momo.savanger.api.budget.dto;

import java.math.BigDecimal;

public class BudgetStatisticsDto {

    private BudgetDto budgetDto;

    private BigDecimal balance;

    private BigDecimal expensesAmount;

    private BigDecimal earningsAmount;

    private BigDecimal debtLendedAmount;

    private BigDecimal debtReceivedAmount;

    private BigDecimal realBalance;
}
