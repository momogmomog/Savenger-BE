package com.momo.savanger.api.debt;

import java.math.BigDecimal;
import java.util.Optional;

public interface DebtService {

    Debt findById(Long id);

    Debt create(CreateDebtDto dto);

    Optional<Debt> findDebt(CreateDebtDto dto);

    BigDecimal getDebtSumByLenderId(Long budgetId);
}
