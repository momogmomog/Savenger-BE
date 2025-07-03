package com.momo.savanger.api.debt;

import java.util.Optional;

public interface DebtService {

    Debt findById(Long id);

    Debt create(CreateDebtDto dto);

    Debt pay(Long id, PayDebtDto dto);

    Optional<Debt> findDebt(Long receiverBudgetId, Long lenderBudgetId);
}
