package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.transaction.dto.CreateTransactionDto;

public interface RecurringTransactionExecutionService {

    RecurringTransaction payPrepayment(Long recurringTransactionId);

    RecurringTransaction execute(
            Long recurringTransactionId,
            CreateTransactionDto transactionOverride
    );
}
