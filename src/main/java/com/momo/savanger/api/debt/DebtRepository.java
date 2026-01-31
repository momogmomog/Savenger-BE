package com.momo.savanger.api.debt;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long>, DebtRepositoryFragment {

    @Query("select coalesce(sum(d.amount), 0) from Debt d where d.receiverBudgetId = :budgetId")
    BigDecimal sumDebtsReceived(Long budgetId);

    @Query("select coalesce(sum(d.amount), 0)  from Debt d where d.lenderBudgetId = :budgetId")
    BigDecimal sumDebtsLended(Long budgetId);
}
