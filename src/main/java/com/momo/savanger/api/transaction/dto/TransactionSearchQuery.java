package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.Transaction_;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
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

    private Long debtId;

    private Boolean noDebtTransactions;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

    private List<Long> tagIds;

    public static TransactionSearchQuery fromAnalyticsQuery(TransactionSearchQueryForAnalytics q) {
        final TransactionSearchQuery query = new TransactionSearchQuery();
        query.setPage(new PageQuery(0, 0));
        query.setSort(new SortQuery(Transaction_.ID, SortDirection.DESC));

        query.setType(q.getType());
        query.setAmount(q.getAmount());
        query.setDateCreated(q.getDateCreated());
        query.setComment(q.getComment());
        query.setRevised(q.getRevised());
        query.setCategoryIds(q.getCategoryIds());
        query.setUserIds(q.getUserIds());
        query.setDebtId(q.getDebtId());
        query.setNoDebtTransactions(q.getNoDebtTransactions());
        query.setBudgetId(q.getBudgetId());
        query.setTagIds(q.getTagIds());

        return query;
    }
}
