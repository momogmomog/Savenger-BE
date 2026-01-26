package com.momo.savanger.api.transfer.dto;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transfer.constraints.ValidTransferDto;
import com.momo.savanger.constraints.NotNull;
import lombok.Data;

@Data
@ValidTransferDto
public class CreateTransferDto {

    @NotNull
    @CanAccessBudget
    private Long sourceBudgetId;

    @NotNull
    @CanAccessBudget
    private Long receiverBudgetId;
}
