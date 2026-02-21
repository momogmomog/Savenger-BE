package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecurringTransactionExecutionServiceImpl implements
        RecurringTransactionExecutionService {

    private final PrepaymentService prepaymentService;
    private final RecurringTransactionService recurringTransactionService;
    private final TransactionService transactionService;

    @Override
    @Transactional
    public RecurringTransaction payPrepayment(Long recurringTransactionId) {
        final RecurringTransaction recurringTransaction = this.recurringTransactionService
                .findByIdFetchAll(recurringTransactionId);

        final Prepayment prepayment = recurringTransaction.getPrepayment();

        if (prepayment.getRemainingAmount().compareTo(recurringTransaction.getAmount()) < 0) {
            recurringTransaction.setAmount(prepayment.getRemainingAmount());
        }

        final var updatedRecurringTransaction = this.execute(recurringTransaction);

        this.prepaymentService.updatePrepaymentAfterPay(prepayment);

        //TODO: TEST scenarios:
        // Recurring transaction completes before prepayment
        if (prepayment.getCompleted()) {
            recurringTransaction.setCompleted(true);
            return this.recurringTransactionService.updateRecurringTransaction(recurringTransaction);
        } else if (updatedRecurringTransaction.getCompleted()) {
            throw ApiException.with(ApiErrorCode.ERR_0023);
        }

        return updatedRecurringTransaction;
    }

    @Override
    @Transactional
    public RecurringTransaction execute(Long recurringTransactionId) {
        //TODO: TEST scenarios:
        // Recurring transaction is completed if the rrule has end date
        // Recurring transaction is completed if the rrule has max occurrences
        // The occurrences field is incremented
        // Next Date is generated properly according to rrule
        return this.execute(
                this.recurringTransactionService.findByIdFetchAll(recurringTransactionId)
        );
    }

    private RecurringTransaction execute(RecurringTransaction recurringTransaction) {
        this.transactionService.createFromRecurringTransaction(
                recurringTransaction
        );
        this.recurringTransactionService.advanceRecurringTransaction(
                recurringTransaction
        );
        return this.recurringTransactionService.updateRecurringTransaction(
                recurringTransaction
        );
    }
}
