package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PrepaymentQuery {

    @Valid
    @NotNull
    private PageQuery page;

    @Valid
    @NotNull
    private SortQuery sort;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

    private BetweenQuery<BigDecimal> amount;

    private String name;

    private BetweenQuery<LocalDateTime> paidUntil;

    private Boolean completed;

    private BetweenQuery<BigDecimal> remainingAmount;
}
