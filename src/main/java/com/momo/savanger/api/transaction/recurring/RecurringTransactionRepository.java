package com.momo.savanger.api.transaction.recurring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long>,
        RecurringTransactionRepositoryFragment {

    boolean existsByIdAndBudgetId(Long recurringTransactionId, Long budgetId);
}
