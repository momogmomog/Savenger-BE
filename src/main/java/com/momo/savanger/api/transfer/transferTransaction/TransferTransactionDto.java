package com.momo.savanger.api.transfer.transferTransaction;

import com.momo.savanger.api.transaction.dto.TransactionDto;
import com.momo.savanger.api.transfer.TransferDto;
import lombok.Data;

@Data
public class TransferTransactionDto {

    private TransferDto transferDto;

    private Long TransferTransactionId;

    private TransactionDto sourceTransactionDto;

    private TransactionDto receiverTransactionDto;
}
