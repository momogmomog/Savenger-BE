package com.momo.savanger.api.debt;

import com.momo.savanger.constants.EntityGraphs;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long>, DebtRepositoryFragment {

    @EntityGraph(EntityGraphs.DEBT_ALL)
    Optional<Debt> findDebtById(Long id);

    @Query("select coalesce(sum(d.amount), 0) from Debt d where d.receiverBudgetId = :budgetId")
    BigDecimal sumDebtsReceived(Long budgetId);

    @Query("select coalesce(sum(d.amount), 0)  from Debt d where d.lenderBudgetId = :budgetId")
    BigDecimal sumDebtsLended(Long budgetId);
}
