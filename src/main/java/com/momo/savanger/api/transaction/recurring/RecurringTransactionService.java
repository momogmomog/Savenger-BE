package com.momo.savanger.api.transaction.recurring;

import java.util.Optional;
import org.springframework.data.domain.Page;

public interface RecurringTransactionService {

    RecurringTransaction create(CreateRecurringTransactionDto dto);

    RecurringTransaction findById(Long id);

    RecurringTransaction findByIdFetchAll(Long id);

    Optional<RecurringTransaction> findByIdIfExists(Long id);

    boolean recurringTransactionExists(Long rTransactionId, Long budgetId);

    void updateRecurringTransaction(RecurringTransaction recurringTransaction);

    Page<RecurringTransaction> search(RecurringTransactionQuery query);
}
