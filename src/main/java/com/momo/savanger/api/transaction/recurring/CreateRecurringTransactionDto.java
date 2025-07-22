package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.constraints.ValidRecurringTransactionDto;
import com.momo.savanger.constants.Lengths;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import com.momo.savanger.constraints.RRule;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@ValidRecurringTransactionDto
public class CreateRecurringTransactionDto {

    @NotNull
    private TransactionType type;

    @NotNull
    @Length(min = 1, max = Lengths.MAX_VARCHAR, message = ValidationMessages.TEXT_MUST_BE_BETWEEN)
    @RRule
    private String recurringRule;

    @NotNull
    private Boolean autoExecute;

    @NotNull
    @MinValueZero
    private BigDecimal amount;

    private Long categoryId;

    @CanAccessBudget
    private Long budgetId;

    private Long debtId;

    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return Objects.requireNonNullElse(this.tagIds, List.of());
    }
}
