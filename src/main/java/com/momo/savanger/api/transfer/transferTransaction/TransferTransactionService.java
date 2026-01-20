package com.momo.savanger.api.transfer.transferTransaction;

public interface TransferTransactionService {

    TransferTransaction create(CreateTransferTransactionDto dto);

    TransferTransactionDto getTransferTransactionDto(Long transferId, Long transferTransactionId);

    TransferTransaction getTransferTransaction(Long transferTransactionId);

    void revertTransferTransaction(Long transferTransactionId);
}
