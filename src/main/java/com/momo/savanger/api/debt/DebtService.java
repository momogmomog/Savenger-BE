package com.momo.savanger.api.debt;

public interface DebtService {

    Debt findById(Long id);

    Debt create(CreateDebtDto dto);

    Boolean validDebt(CreateDebtDto dto);
}
