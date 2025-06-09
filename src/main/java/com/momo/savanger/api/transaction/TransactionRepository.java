package com.momo.savanger.api.transaction;

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

}
