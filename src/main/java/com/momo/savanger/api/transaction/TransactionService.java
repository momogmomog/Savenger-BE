package com.momo.savanger.api.transaction;

import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.user.User;
import org.springframework.data.domain.Page;

public interface TransactionService {

    Transaction findById(Long id);

    Transaction create(CreateTransactionDto dto, User user);

    Page<Transaction> searchTransactions(TransactionSearchQuery query, User user);

    Transaction edit(Long id, EditTransactionDto dto);

    Boolean existsByIdAndRevisedFalse(Long id);

    void deleteById(Long id);

    boolean canDeleteTransaction(Long transactionId, User user);

    boolean canViewTransaction(Long transactionId, Long userId);

    void revisedTransactions(Long budgetId);
}

