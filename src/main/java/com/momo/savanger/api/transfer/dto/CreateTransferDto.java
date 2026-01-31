package com.momo.savanger.api.transfer.dto;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.constraints.NoneEqual;
import com.momo.savanger.constraints.NotNull;
import lombok.Data;

@Data
@NoneEqual(fields = {"sourceBudgetId", "receiverBudgetId"},
        message = "Source budget id and receiver budget id should be different."
)
public class CreateTransferDto {

    @NotNull
    @CanAccessBudget
    private Long sourceBudgetId;

    @NotNull
    @CanAccessBudget
    private Long receiverBudgetId;
}
