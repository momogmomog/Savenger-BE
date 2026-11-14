package com.momo.savanger.api.debt;

import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface DebtService {

    Debt findById(Long id);

    Debt findAndFetchDetails(Long id);

    Debt create(CreateDebtDto dto);

    Debt pay(Long id, PayDebtDto dto);

    Debt pay(
            Long id,
            PayDebtDto dto,
            RecurringTransaction recurringTransaction,
            CreateTransactionDto transactionOverride
    );

    Optional<Debt> findDebt(Long receiverBudgetId, Long lenderBudgetId);

    Boolean isValid(Long id, Long budgetId);

    Optional<Debt> findDebtIfExists(Long id, Long budgetId);

    Page<Debt> search(DebtQuery query);
}
