package com.momo.savanger.api.transfer.transferTransaction;

import com.momo.savanger.api.transaction.dto.TransactionDto;
import com.momo.savanger.api.transfer.dto.TransferDto;
import lombok.Data;

@Data
public class TransferTransactionDto {

    private TransferDto transfer;

    private Long TransferTransactionId;

    private TransactionDto sourceTransaction;

    private TransactionDto receiverTransaction;
}
