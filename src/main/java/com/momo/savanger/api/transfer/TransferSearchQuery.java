package com.momo.savanger.api.transfer;

import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TransferSearchQuery {

    @Valid
    @NotNull
    private PageQuery page;

    @Valid
    @NotNull
    private SortQuery sort;

    @NotNull
    private Long sourceBudgetId;

    private Long receiverBudgetId;

    private Boolean active;
}
