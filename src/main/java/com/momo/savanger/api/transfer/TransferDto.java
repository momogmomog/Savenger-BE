package com.momo.savanger.api.transfer;

import lombok.Data;

@Data
public class TransferDto {

    private int id;

    private Long sourceBudgetId;

    private Long receiverBudgetId;

    private Boolean active;
}
