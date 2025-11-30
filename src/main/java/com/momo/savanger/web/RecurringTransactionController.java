package com.momo.savanger.web;

import com.momo.savanger.api.transaction.recurring.RTransactionPrepaymentService;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionMapper;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.api.transaction.recurring.constraints.ValidRecurringTransaction;
import com.momo.savanger.constants.Endpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;
    private final RTransactionPrepaymentService rTransactionPrepaymentService;
    private final RecurringTransactionMapper recurringTransactionMapper;

    @PostMapping(Endpoints.PAY_PREPAYMENT)
    public RecurringTransactionDto pay(
            @PathVariable("rTransactionId") @ValidRecurringTransaction Long rTransactionId) {

        return this.recurringTransactionMapper.toRecurringTransactionDto(
                this.rTransactionPrepaymentService.pay(rTransactionId)
        );
    }
}
