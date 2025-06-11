package com.momo.savanger.api.budget.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class StatisticDto {

    private BudgetDto budget;

    private BigDecimal balance;

    private BigDecimal expensesAmount;

    private BigDecimal earningsAmount;
}
