package com.momo.savanger.api.transaction;

import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.transaction.dto.CreateTransactionServiceDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.user.User;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

public interface TransactionService {

    Transaction findById(Long id);

    Transaction findAndFetchDetails(Long id);

    Transaction create(CreateTransactionServiceDto dto, Long userId);

    Transaction createPrepaymentTransaction(RecurringTransaction recurringTransaction);

    Page<Transaction> searchTransactions(TransactionSearchQuery query, User user);

    Transaction edit(Long id, EditTransactionDto dto);

    Boolean existsByIdAndRevisedFalse(Long id);

    void deleteById(Long id);

    boolean canDeleteTransaction(Long transactionId, User user);

    boolean canViewTransaction(Long transactionId, Long userId);

    void reviseTransactions(Long budgetId);

    BigDecimal getExpensesAmount(Long budgetId);

    BigDecimal getEarningsAmount(Long budgetId);

    BigDecimal getDebtLendedAmount(Long budgetId);

    BigDecimal getDebtReceivedAmount(Long budgetId);

    void createDebtTransactions(Debt debt, BigDecimal amount);

    void payDebtTransaction(Debt debt, BigDecimal amount);

    Transaction createCompensationTransaction(Long budgetId, BigDecimal amount);

    BigDecimal getPrepaymentPaidAmount(Long prepaymentId);

    List<Long> extractCategoryIds(TransactionSearchQuery query);

    List<Long> extractTagIds(TransactionSearchQuery query);

    BigDecimal sum(TransactionSearchQuery query);
}

