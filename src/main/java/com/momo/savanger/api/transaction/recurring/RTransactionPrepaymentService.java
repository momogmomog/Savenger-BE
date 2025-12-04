package com.momo.savanger.api.transaction.recurring;

public interface RTransactionPrepaymentService {

    RecurringTransaction pay(Long recurringTransactionId);
}
