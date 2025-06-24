package com.momo.savanger.api.debt;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.debt.constraints.ValidDebtDto;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
@ValidDebtDto
public class CreateDebtDto {

    @CanAccessBudget
    @NotNull
    private Long receiverBudgetId;

    @CanAccessBudget
    @NotNull
    private Long lenderBudgetId;

    @MinValueZero
    @NotNull
    private BigDecimal debtAmount;

}
