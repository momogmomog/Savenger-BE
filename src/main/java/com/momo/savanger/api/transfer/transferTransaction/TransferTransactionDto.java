package com.momo.savanger.api.transfer.transferTransaction;

import com.momo.savanger.api.transaction.dto.TransactionDto;
import com.momo.savanger.api.transfer.dto.TransferFullDto;
import lombok.Data;

@Data
public class TransferTransactionDto {

    private TransferFullDto transfer;

    private Long transferTransactionId;

    private TransactionDto sourceTransaction;

    private TransactionDto receiverTransaction;
}
