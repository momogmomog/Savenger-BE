package com.momo.savanger.api.prepayment;

import java.math.BigDecimal;

public interface PrepaymentService {

    Prepayment findById(Long id);

    Prepayment create(CreatePrepaymentDto dto);

    void updatePrepaymentAfterPay(Prepayment prepayment);

    BigDecimal getRemainingPrepaymentAmountSumByBudgetId(Long id);

}
