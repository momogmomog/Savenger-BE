package com.momo.savanger.api.transfer.transferTransaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferTransactionServiceImpl implements TransferTransactionService {

    private final TransferTransactionRepository transferTransactionRepository;

    @Override
    public TransferTransaction create(CreateTransferTransactionDto dto) {
    TransferTransaction transferTransaction = new TransferTransaction();
    transferTransaction.setTransferId(dto.getTransferId());

    this.transferTransactionRepository.saveAndFlush(transferTransaction);


    }
}
