package com.momo.savanger.api.budget.dto;

import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BudgetSearchQuery {

    @Valid
    @NotNull
    private PageQuery page;

    @Valid
    @NotNull
    private SortQuery sort;

    private String budgetName;

    private BetweenQuery<LocalDateTime> dateStarted;

    private BetweenQuery<LocalDateTime> dueDate;

    private Boolean active;

    private BetweenQuery<BigDecimal> balance;

    private BetweenQuery<BigDecimal> budgetCap;

    private Boolean autoRevise;

}
