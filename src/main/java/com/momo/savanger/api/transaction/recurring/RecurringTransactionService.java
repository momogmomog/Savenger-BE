package com.momo.savanger.api.transaction.recurring;

import java.util.Optional;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

public interface RecurringTransactionService {

    RecurringTransaction create(CreateRecurringTransactionDto dto)
            throws InvalidRecurrenceRuleException;

    RecurringTransaction findById(Long id);

    Optional<RecurringTransaction> findByIdIfExists(Long id);

    void addPrepaymentId(Long prepaymentId, RecurringTransaction recurringTransaction);

    boolean recurringTransactionExists(Long rTransactionId, Long budgetId);

}
