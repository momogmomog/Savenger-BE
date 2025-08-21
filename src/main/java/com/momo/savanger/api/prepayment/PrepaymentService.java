package com.momo.savanger.api.prepayment;

import java.math.BigDecimal;

public interface PrepaymentService {

    Prepayment findById(Long id);

    Prepayment create(CreatePrepaymentDto dto);

    BigDecimal getPrepaymentAmountSumByBudgetId(Long id);

}
