package com.momo.savanger.api.category;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CategoryQuery {
    @Valid
    @NotNull
    private PageQuery page;

    @Valid
    @NotNull
    private SortQuery sort;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

    private String categoryName;

    private BetweenQuery<BigDecimal> budgetCap;
}
