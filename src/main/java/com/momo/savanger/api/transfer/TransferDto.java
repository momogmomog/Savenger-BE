package com.momo.savanger.api.transfer;

import com.momo.savanger.api.budget.dto.BudgetDto;
import lombok.Data;

@Data
public class TransferDto {

    private int id;

    private Long sourceBudgetId;

    private Long receiverBudgetId;

    private Boolean active;

    private BudgetDto receiverBudget;

    private BudgetDto sourceBudget;

}
