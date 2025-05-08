package com.momo.savanger.api.transaction;

import com.momo.savanger.api.user.User;

public interface TransactionService {

    Transaction findById(Long id);

    Transaction create(CreateTransactionDto dto, User user);
}
