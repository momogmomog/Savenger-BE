package com.momo.savanger.api.debt;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long>, DebtRepositoryFragment {

    @Query("select sum(t.amount) from Debt t "
            + "where t.lenderBudgetId = :budgetId")
    BigDecimal sumDebtByLenderBudgetId(Long budgetId);
}
