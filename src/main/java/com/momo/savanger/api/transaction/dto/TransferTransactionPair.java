package com.momo.savanger.api.transaction.dto;

import lombok.Data;

@Data
public class TransferTransactionPair {

    TransactionDtoSimple sourceTransaction;

    TransactionDtoSimple receiverTransaction;
}
