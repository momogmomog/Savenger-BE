package com.momo.savanger.api.debt;

import java.math.BigDecimal;

public interface DebtService {

    Debt findById(Long id);

    Debt create(CreateDebtDto dto);

    Boolean isDebtValid(CreateDebtDto dto);

    BigDecimal getDebtSumByLenderId(Long budgetId);
}
