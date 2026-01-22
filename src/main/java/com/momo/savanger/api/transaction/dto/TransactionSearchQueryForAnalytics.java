package com.momo.savanger.api.transaction.dto;

import static com.momo.savanger.constraints.ConditionallyRequiredStrategy.WHEN_COND_FIELD_IS_NULL;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.constraints.ConditionallyRequired;
import com.momo.savanger.constraints.IfBoolEqualsNoneMustBeNull;
import com.momo.savanger.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@IfBoolEqualsNoneMustBeNull(boolField = "revised", valueToCheck = true, fields = "dateCreated")
@ConditionallyRequired(conditionField = "revised", strategy = WHEN_COND_FIELD_IS_NULL, targetField = "dateCreated")
public class TransactionSearchQueryForAnalytics {

    private TransactionType type;

    private BetweenQuery<BigDecimal> amount;

    private BetweenQuery<LocalDateTime> dateCreated;

    private String comment;

    private Boolean revised;

    private List<Long> categoryIds;

    private List<Long> userIds;

    private Long debtId;

    private Boolean noDebtTransactions;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

    private List<Long> tagIds;
}
