package com.momo.savanger.api.prepayment;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PrepaymentRepository extends JpaRepository<Prepayment, Long>,
        PrepaymentRepositoryFragment {

    @Query("select sum(p.amount) from Prepayment p "
            + " where p.budgetId = :budgetId "
            + "and p.completed = false ")
    BigDecimal sumPrepaymentAmountByBudgetId(Long budgetId);

}
