package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.recurringRule.RecurringRuleService;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrepaymentServiceImpl implements PrepaymentService {

    private final PrepaymentRepository prepaymentRepository;

    private final PrepaymentMapper prepaymentMapper;

    private final RecurringTransactionService recurringTransactionService;

    private final RecurringRuleService recurringRuleService;

    @Override
    public Prepayment findById(Long id) {
        return this.prepaymentRepository.findById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0015)
                );
    }

    @Override
    @Transactional
    public Prepayment create(CreatePrepaymentDto dto) throws InvalidRecurrenceRuleException {
        final Prepayment prepayment = this.prepaymentMapper.toPrepayment(dto);

        prepayment.setCompleted(false);
        prepayment.setRemainingAmount(
                dto.getAmount()
        );

        this.prepaymentRepository.saveAndFlush(prepayment);

        final RecurringTransaction rTransaction;

        if (dto.getRecurringTransaction() != null) {
            rTransaction = this.recurringTransactionService.create(
                    dto.getRecurringTransaction()
            );
        } else if (dto.getRecurringTransactionId() != null) {
            rTransaction = this.recurringTransactionService.findById(
                    dto.getRecurringTransactionId()
            );

            //I think we should set create date to existed rTransaction
            //I'm not sure that is date we need?
            rTransaction.setCreateDate(LocalDateTime.now());
        } else {
            rTransaction = null;
        }

        if (rTransaction != null) {
            this.recurringTransactionService.addPrepaymentId(prepayment.getId(), rTransaction);
            rTransaction.setNextDate(this.recurringRuleService.convertRecurringRuleToDate(
                    rTransaction.getRecurringRule(), rTransaction.getCreateDate()));
        }

        return this.findById(
                prepayment.getId()
        );
    }

    @Override
    @Transactional
    public Prepayment pay(Long recurringTransactionId) throws InvalidRecurrenceRuleException {

        final RecurringTransaction recurringTransaction = this.recurringTransactionService.findByIdIfExists(
                recurringTransactionId).orElseThrow();

        final Prepayment prepayment = this.findById(recurringTransaction.getPrepaymentId());

        if (prepayment.getAmount().compareTo(recurringTransaction.getAmount()) < 0) {
            recurringTransaction.setAmount(prepayment.getAmount());
        }

        if (recurringTransaction.getNextDate().toLocalDate()
                .equals(LocalDateTime.now().toLocalDate())) {

            this.updatePrepaymentAfterPay(prepayment, recurringTransaction.getAmount());

            LocalDateTime nextDate = this.recurringRuleService.convertRecurringRuleToDate(
                    recurringTransaction.getRecurringRule(), recurringTransaction.getNextDate()
            );

            this.recurringTransactionService.updateRTransactionAfterPay(
                    recurringTransaction, nextDate, prepayment
            );

        } else {
            //Maybe we don't need this?
            this.updatePrepaymentAfterPay(prepayment, recurringTransaction.getAmount());
        }

        return prepayment;
    }

    @Override
    @Transactional
    public Prepayment updatePrepaymentAfterPay(Prepayment prepayment, BigDecimal amount) {

        prepayment.setRemainingAmount(
                prepayment.getRemainingAmount().subtract(amount)
        );

        if (prepayment.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            prepayment.setCompleted(true);
        }

        this.prepaymentRepository.save(prepayment);

        return prepayment;
    }

    @Override
    public BigDecimal getRemainingPrepaymentAmountSumByBudgetId(Long budgetId) {
        final BigDecimal sum = this.prepaymentRepository.sumPrepaymentAmountByBudgetId(budgetId);

        if (sum == null) {
            return BigDecimal.ZERO;
        }
        return sum;
    }
}
