package com.momo.savanger.api.prepayment;

import java.math.BigDecimal;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

public interface PrepaymentService {

    Prepayment findById(Long id);

    Prepayment create(CreatePrepaymentDto dto) throws InvalidRecurrenceRuleException;

    Prepayment pay(Long recurringTransactionId) throws InvalidRecurrenceRuleException;

    Prepayment updatePrepaymentAfterPay(Prepayment prepayment, BigDecimal amount);

    BigDecimal getRemainingPrepaymentAmountSumByBudgetId(Long id);

}
