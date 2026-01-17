package com.momo.savanger.api.transaction.dto;

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
public class TransactionSearchQuery {

    @Valid
    @NotNull
    private PageQuery page;

    @Valid
    @NotNull
    private SortQuery sort;

    private TransactionType type;

    private BetweenQuery<BigDecimal> amount;

    private BetweenQuery<LocalDateTime> dateCreated;

    private String comment;

    private Boolean revised;

    private List<Long> categoryIds;

    private List<Long> userIds;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

    private List<Long> tagIds;
}
