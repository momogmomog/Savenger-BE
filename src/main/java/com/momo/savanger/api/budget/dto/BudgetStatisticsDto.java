package com.momo.savanger.api.budget.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class BudgetStatisticsDto {

    private BudgetDto budget;

    private BigDecimal balance;

    private BigDecimal expensesAmount;

    private BigDecimal earningsAmount;

    private BigDecimal debtLendedAmount;

    private BigDecimal debtReceivedAmount;

    private BigDecimal realBalance;
}
