package com.momo.savanger.api.prepayment;

public interface PrepaymentService {

    Prepayment findById(Long id);

    Prepayment create(CreatePrepaymentDto dto);

    Boolean isPrepaymentValid(Long prepaymentId, Long budgetId);

}
