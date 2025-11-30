package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.recurringRule.RecurringRuleService;
import com.momo.savanger.api.transaction.TransactionService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RTransactionPrepaymentServiceImpl implements RTransactionPrepaymentService {

    private final PrepaymentService prepaymentService;
    private final RecurringTransactionService recurringTransactionService;
    private final TransactionService transactionService;
    private final RecurringRuleService recurringRuleService;

    @Override
    public RecurringTransaction pay(Long recurringTransactionId) {
        final RecurringTransaction recurringTransaction = this.recurringTransactionService.findByIdFetchAll(
                recurringTransactionId);

        final Prepayment prepayment = recurringTransaction.getPrepayment();

        if (prepayment.getRemainingAmount().compareTo(recurringTransaction.getAmount()) < 0) {
            recurringTransaction.setAmount(prepayment.getRemainingAmount());
        }

        this.transactionService.createPrepaymentTransaction(recurringTransaction);

        this.prepaymentService.updatePrepaymentAfterPay(prepayment);

        if (prepayment.getCompleted()) {
            recurringTransaction.setCompleted(true);
        } else {
            final LocalDateTime nextDate = this.recurringRuleService
                    .convertRecurringRuleToDate(
                            recurringTransaction.getRecurringRule(),
                            recurringTransaction.getNextDate()
                    );

            recurringTransaction.setNextDate(nextDate);
        }

        this.recurringTransactionService.updateRecurringTransaction(recurringTransaction);

        return recurringTransaction;
    }
}
