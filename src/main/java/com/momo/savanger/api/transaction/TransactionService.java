package com.momo.savanger.api.transaction;

import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.user.User;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;

public interface TransactionService {

    Transaction findById(Long id);

    Transaction create(CreateTransactionDto dto, Long userId);

    Page<Transaction> searchTransactions(TransactionSearchQuery query, User user);

    Transaction edit(Long id, EditTransactionDto dto);

    Boolean existsByIdAndRevisedFalse(Long id);

    void deleteById(Long id);

    boolean canDeleteTransaction(Long transactionId, User user);

    boolean canViewTransaction(Long transactionId, Long userId);

    void reviseTransactions(Long budgetId);

    BigDecimal getExpensesAmount(Long budgetId);

    BigDecimal getEarningsAmount(Long budgetId);

    void createDebtTransactions(Debt debt, BigDecimal amount);

    void payDebtTransaction(Debt debt, BigDecimal amount);

    Transaction createCompensationTransaction(Long budgetId, BigDecimal amount);
}

