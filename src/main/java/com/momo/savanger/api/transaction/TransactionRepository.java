package com.momo.savanger.api.transaction;

import com.momo.savanger.constants.EntityGraphs;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        TransactionRepositoryFragment {

    boolean existsByIdAndRevisedFalse(Long id);

    @EntityGraph(EntityGraphs.TRANSACTION_DETAILED)
    Optional<Transaction> findTransactionById(Long id);

    @Query("select count(t) > 0 from Transaction t"
            + " left join t.budget budget "
            + " left join budget.participants participant "
            + " where t.id = :transactionId and (budget.ownerId = :userId or participant.id = :userId)")
    boolean existOwnerOrParticipant(Long transactionId, Long userId);

    @Query("select sum(t.amount) from Transaction t "
            + " where t.budgetId = :budgetId and t.revised = false "
            + " and t.type = :type"
            + " and t.debtId is null ")
    BigDecimal sumAmountByBudgetIdAndTypeOfNonRevisedNonDebt(Long budgetId,
            TransactionType type);

    @Query("select sum(t.amount) from Transaction t "
            + " where t.budgetId = :budgetId and t.revised = false and t.type = :type and t.debtId is not null ")
    BigDecimal sumDebtAmountByBudgetIdAndTypeOfNonRevised(Long budgetId, TransactionType type);

    @Query("select sum(t.amount) from Transaction t where t.type = :type and t.prepaymentId = :prepaymentId")
    BigDecimal sumByPrepaymentIdAndType(TransactionType type, Long prepaymentId);

    @Modifying
    @Query("update Transaction t "
            + " set t.revised = true "
            + " where t.revised = false and t.budgetId = :budgetId")
    void setRevisedTrue(Long budgetId);
}
