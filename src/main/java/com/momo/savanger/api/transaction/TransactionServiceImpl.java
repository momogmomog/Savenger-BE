package com.momo.savanger.api.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction findById(Long id) {
        return this.transactionRepository.findById(id).orElse(null);
    }
}
