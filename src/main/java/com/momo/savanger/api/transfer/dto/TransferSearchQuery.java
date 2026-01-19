package com.momo.savanger.api.transfer.dto;

import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;
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

    private List<Long> receiverBudgetIds;

    private Boolean active;
}
