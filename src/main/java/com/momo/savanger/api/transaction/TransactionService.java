package com.momo.savanger.api.transaction;

import com.momo.savanger.api.user.User;
import org.springframework.data.web.PagedModel;

public interface TransactionService {

    Transaction findById(Long id);

    Transaction create(CreateTransactionDto dto, User user);

    PagedModel<Transaction> searchTransactions(TransactionSearchQuery query);
}
