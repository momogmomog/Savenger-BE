package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
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
        return this.payPrepayment(
                this.recurringTransactionService.findByIdFetchAll(recurringTransactionId),
                null
        );
    }

    private RecurringTransaction payPrepayment(
            RecurringTransaction recurringTransaction,
            CreateTransactionDto transactionOverride
    ) {
        final Prepayment prepayment = recurringTransaction.getPrepayment();

        if (prepayment.getRemainingAmount().compareTo(recurringTransaction.getAmount()) < 0) {
            recurringTransaction.setAmount(prepayment.getRemainingAmount());
        }

        if (transactionOverride != null
                && transactionOverride.getAmount() != null
                && prepayment.getRemainingAmount().compareTo(transactionOverride.getAmount()) < 0) {
            transactionOverride.setAmount(prepayment.getRemainingAmount());
        }

        final var updatedRecurringTransaction = this.execute(
                recurringTransaction,
                transactionOverride
        );

        this.prepaymentService.updatePrepaymentAfterPay(prepayment);

        //TODO: TEST scenarios:
        // Recurring transaction completes before prepayment
        if (prepayment.getCompleted()) {
            recurringTransaction.setCompleted(true);
            return this.recurringTransactionService.updateRecurringTransaction(
                    recurringTransaction);
        } else if (updatedRecurringTransaction.getCompleted()) {
            throw ApiException.with(ApiErrorCode.ERR_0023);
        }

        return updatedRecurringTransaction;
    }

    @Override
    @Transactional
    public RecurringTransaction execute(
            Long recurringTransactionId,
            CreateTransactionDto transactionOverride) {
        //TODO: TEST scenarios:
        // Recurring transaction is completed if the rrule has end date
        // Recurring transaction is completed if the rrule has max occurrences
        // The occurrences field is incremented
        // Next Date is generated properly according to rrule

        final var rTransaction = this.recurringTransactionService.findByIdFetchAll(
                recurringTransactionId);

        if (rTransaction.getPrepaymentId() != null) {
            return this.payPrepayment(rTransaction, transactionOverride);
        }
        return this.execute(
                rTransaction,
                transactionOverride
        );
    }

    private RecurringTransaction execute(
            RecurringTransaction recurringTransaction,
            CreateTransactionDto transactionOverride
    ) {
        this.transactionService.createFromRecurringTransaction(
                recurringTransaction,
                transactionOverride
        );
        this.recurringTransactionService.advanceRecurringTransaction(
                recurringTransaction
        );
        return this.recurringTransactionService.updateRecurringTransaction(
                recurringTransaction
        );
    }
}
