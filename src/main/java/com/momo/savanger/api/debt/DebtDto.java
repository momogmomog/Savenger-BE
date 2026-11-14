package com.momo.savanger.api.debt;

import com.momo.savanger.api.budget.dto.BudgetSimpleDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DebtDto {

    private Long id;

    private Long receiverBudgetId;

    private Long lenderBudgetId;

    private BigDecimal amount;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private BudgetSimpleDto receiverBudget;

    private BudgetSimpleDto lenderBudget;
}
