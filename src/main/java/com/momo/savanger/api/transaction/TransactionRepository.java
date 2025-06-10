package com.momo.savanger.api.transaction;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        TransactionRepositoryFragment {

    boolean existsByIdAndRevisedFalse(Long id);

    @Query("select count(t) > 0 from Transaction t"
            + " left join t.budget budget "
            + " left join budget.participants participant "
            + " where t.id = :transactionId and (budget.ownerId = :userId or participant.id = :userId)")
    boolean existOwnerOrParticipant(Long transactionId, Long userId);

    @Query("select sum(t.amount) from Transaction t "
            + " where t.budgetId = :budgetId and t.revised = false and t.type = :type")
    BigDecimal sumAmountByBudgetIdAndTypeOfNonRevised(Long budgetId, TransactionType type);
}
