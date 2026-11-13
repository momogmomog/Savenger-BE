package com.momo.savanger.api.prepayment;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PrepaymentService {

    Prepayment findById(Long id);

    Optional<Prepayment> findByIdIfExists(Long id);

    Optional<Prepayment> findByIdIfExists(Long id, boolean allowCompleted);

    Prepayment create(CreatePrepaymentDto dto);

    Prepayment edit(Long id, CreatePrepaymentDto dto);

    void updatePrepaymentAfterPay(Prepayment prepayment);

    BigDecimal getRemainingPrepaymentAmountSumByBudgetId(Long id);

    Page<Prepayment> search(PrepaymentQuery query);

}
