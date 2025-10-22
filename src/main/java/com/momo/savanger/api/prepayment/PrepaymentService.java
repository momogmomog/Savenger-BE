package com.momo.savanger.api.prepayment;

import java.math.BigDecimal;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

public interface PrepaymentService {

    Prepayment findById(Long id);

    Prepayment create(CreatePrepaymentDto dto) throws InvalidRecurrenceRuleException;

    BigDecimal getRemainingPrepaymentAmountSumByBudgetId(Long id);

}
