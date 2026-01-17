package com.momo.savanger.api.transaction.dto;

import lombok.Data;

@Data
public class TransferTransactionPair {

    TransactionDto sourceTransaction;

    TransactionDto receiverTransaction;
}
