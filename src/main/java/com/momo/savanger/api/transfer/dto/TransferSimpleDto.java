package com.momo.savanger.api.transfer.dto;

import com.momo.savanger.api.budget.dto.BudgetSimpleDto;
import lombok.Data;

@Data
public class TransferSimpleDto {

    private int id;

    private Long sourceBudgetId;

    private Long receiverBudgetId;

    private Boolean active;

    private BudgetSimpleDto receiverBudget;
}
