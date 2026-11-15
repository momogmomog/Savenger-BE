package com.momo.savanger.api.debt;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class DebtQuery {

    @Valid
    @NotNull
    private PageQuery page;

    @Valid
    @NotNull
    private SortQuery sort;

    @CanAccessBudget
    private Long receiverBudgetId;

    @CanAccessBudget
    private Long lenderBudgetId;

    private String receiverName;

    private List<Long> budgetIds;

    private BetweenQuery<BigDecimal> amount;
}
