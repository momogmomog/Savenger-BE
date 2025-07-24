package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.constants.Lengths;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import com.momo.savanger.converter.DateTimeConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CreatePrepaymentDto {

    @MinValueZero
    @NotNull
    private BigDecimal amount;

    @Size(min = 2, max = Lengths.MAX_NAME)
    @NotNull
    private String name;

    @DateTimeConverter
    @NotNull
    private LocalDateTime paidUntil;

    @CanAccessBudget
    @NotNull
    private Long budgetId;

    private Long recurringTransactionId;

    @Valid
    private CreateRecurringTransactionDto recurringTransactionDto;

}
