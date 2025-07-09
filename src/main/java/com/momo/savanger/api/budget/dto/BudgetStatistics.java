package com.momo.savanger.api.budget.dto;

import com.momo.savanger.api.budget.Budget;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class BudgetStatistics {

    private Budget budget;

    private BigDecimal balance;

    private BigDecimal expensesAmount;

    private BigDecimal earningsAmount;

    private BigDecimal debtLendedAmount;

    private BigDecimal debtReceivedAmount;

    private BigDecimal realBalance;
}
