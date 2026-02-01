package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RecurringTransactionDto {

    private Long id;

    private TransactionType type;

    private String recurringRule;

    private LocalDateTime nextDate;

    private Boolean autoExecute;

    private BigDecimal amount;

    private Long prepaymentId;

    private Boolean completed;

    private Long categoryId;

    private Long budgetId;

    private Long debtId;
}
