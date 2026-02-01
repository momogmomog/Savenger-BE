package com.momo.savanger.web;

import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RTransactionPrepaymentService;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionMapper;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionQuery;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.api.transaction.recurring.constraints.ValidRecurringTransaction;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;
    private final RTransactionPrepaymentService rTransactionPrepaymentService;
    private final RecurringTransactionMapper recurringTransactionMapper;

    @PostMapping(Endpoints.PAY_R_TRANSACTION)
    public RecurringTransactionDto pay(
            @PathVariable("rTransactionId") @ValidRecurringTransaction Long rTransactionId) {

        return this.recurringTransactionMapper.toRecurringTransactionDto(
                this.rTransactionPrepaymentService.pay(rTransactionId)
        );
    }

    @PostMapping(Endpoints.RECURRING_TRANSACTIONS)
    public RecurringTransactionDto createRecurringTransaction(
            @Valid @RequestBody CreateRecurringTransactionDto dto) {
        return this.recurringTransactionMapper.toRecurringTransactionDto(
                this.recurringTransactionService.create(dto)
        );
    }

    @PostMapping(Endpoints.RECURRING_TRANSACTIONS_SEARCH)
    public PagedModel<RecurringTransactionDto> searchRecurringTransactions(
            @Valid @RequestBody RecurringTransactionQuery query) {
        return new PagedModel<>(
                this.recurringTransactionService
                        .search(query)
                        .map(this.recurringTransactionMapper::toRecurringTransactionDto)
        );
    }
}
