package com.momo.savanger.api.transaction.recurring;

import java.util.Optional;

public interface RecurringTransactionService {

    RecurringTransaction create(CreateRecurringTransactionDto dto);

    RecurringTransaction findById(Long id);

    Optional<RecurringTransaction> findByIdIfExists(Long id);

    void addPrepaymentId(Long prepaymentId, RecurringTransaction recurringTransaction);

    boolean recurringTransactionExists(Long rTransactionId, Long budgetId);

    void updateRecurringTransaction(RecurringTransaction recurringTransaction);

}
