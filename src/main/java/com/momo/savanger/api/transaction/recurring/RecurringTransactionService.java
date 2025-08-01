package com.momo.savanger.api.transaction.recurring;

public interface RecurringTransactionService {

    RecurringTransaction create(CreateRecurringTransactionDto dto);

    RecurringTransaction findById(Long id);

    void addPrepaymentId(Long prepaymentId, RecurringTransaction recurringTransaction);

    Boolean isRecurringTransactionValid(Long rTransactionId);

}
