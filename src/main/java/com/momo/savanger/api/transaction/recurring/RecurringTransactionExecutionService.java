package com.momo.savanger.api.transaction.recurring;

public interface RecurringTransactionExecutionService {

    RecurringTransaction payPrepayment(Long recurringTransactionId);

    RecurringTransaction execute(Long recurringTransactionId);
}
