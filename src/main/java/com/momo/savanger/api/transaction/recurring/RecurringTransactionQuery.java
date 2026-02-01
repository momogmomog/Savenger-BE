package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class RecurringTransactionQuery {

    @Valid
    @NotNull
    private PageQuery page;

    @Valid
    @NotNull
    private SortQuery sort;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

    private TransactionType type;

    private BetweenQuery<LocalDateTime> nextDate;

    private Boolean autoExecute;

    private BetweenQuery<BigDecimal> amount;

    private Long prepaymentId;

    private Boolean completed;

    private List<Long> categoryIds;

    private Long debtId;

    private List<Long> tagIds;
}
