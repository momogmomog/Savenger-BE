package com.momo.savanger.api.transfer.dto;

import com.momo.savanger.api.budget.dto.BudgetSimpleDto;
import lombok.Data;

@Data
public class TransferFullDto {

    private Long id;

    private Long sourceBudgetId;

    private Long receiverBudgetId;

    private Boolean active;

    private BudgetSimpleDto receiverBudget;

    private BudgetSimpleDto sourceBudget;

}
