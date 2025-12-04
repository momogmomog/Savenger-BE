package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.recurringRule.RecurringRuleService;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrepaymentServiceImpl implements PrepaymentService {

    private final PrepaymentRepository prepaymentRepository;

    private final PrepaymentMapper prepaymentMapper;

    private final RecurringTransactionService recurringTransactionService;

    private final RecurringRuleService recurringRuleService;

    private final TransactionService transactionService;

    @Override
    public Prepayment findById(Long id) {
        return this.prepaymentRepository.findById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0015)
                );
    }

    @Override
    @Transactional
    public Prepayment create(CreatePrepaymentDto dto) {
        final Prepayment prepayment = this.prepaymentMapper.toPrepayment(dto);

        prepayment.setCompleted(false);
        prepayment.setRemainingAmount(
                dto.getAmount()
        );

        this.prepaymentRepository.saveAndFlush(prepayment);

        final RecurringTransaction rTransaction;

        if (dto.getRecurringTransaction() != null) {
            dto.getRecurringTransaction().setType(TransactionType.EXPENSE);

            rTransaction = this.recurringTransactionService.create(
                    dto.getRecurringTransaction()
            );
        } else if (dto.getRecurringTransactionId() != null) {
            rTransaction = this.recurringTransactionService.findById(
                    dto.getRecurringTransactionId()
            );
        } else {
            rTransaction = null;
        }

        if (rTransaction != null) {

            rTransaction.setPrepaymentId(prepayment.getId());
            rTransaction.setNextDate(this.recurringRuleService
                    .convertRecurringRuleToDate(
                            rTransaction.getRecurringRule(),
                            rTransaction.getCreateDate()
                    )
            );

            this.recurringTransactionService.updateRecurringTransaction(rTransaction);
        }

        return this.findById(
                prepayment.getId()
        );
    }

    @Override
    public BigDecimal getRemainingPrepaymentAmountSumByBudgetId(Long budgetId) {
        final BigDecimal sum = this.prepaymentRepository.sumPrepaymentAmountByBudgetId(budgetId);

        if (sum == null) {
            return BigDecimal.ZERO;
        }
        return sum;
    }

    @Override
    public void updatePrepaymentAfterPay(Prepayment prepayment) {

        prepayment.setRemainingAmount(
                prepayment.getAmount().subtract(
                        this.transactionService.getPrepaymentPaidAmount(
                                prepayment.getId()
                        )
                )
        );

        if (prepayment.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            prepayment.setCompleted(true);
        }

        prepayment.setUpdateDate(LocalDateTime.now());

        this.prepaymentRepository.save(prepayment);
    }
}
