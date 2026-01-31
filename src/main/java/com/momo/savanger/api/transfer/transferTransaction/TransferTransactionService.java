package com.momo.savanger.api.transfer.transferTransaction;

public interface TransferTransactionService {

    TransferTransactionDto create(CreateTransferTransactionDto dto);

    TransferTransactionDto getTransferTransactionDto(Long transferTransactionId);

    TransferTransaction getTransferTransaction(Long transferTransactionId);

    void revertTransferTransaction(Long transferTransactionId);
}
