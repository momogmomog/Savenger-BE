package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrepaymentServiceImpl implements PrepaymentService {

    private final PrepaymentRepository prepaymentRepository;

    private final PrepaymentMapper prepaymentMapper;

    private final BudgetService budgetService;

    private final RecurringTransactionService recurringTransactionService;

    @Override
    public Prepayment findById(Long id) {
        return this.prepaymentRepository.findById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0016)
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

        final BudgetStatistics statistics = this.budgetService.getStatistics(
                dto.getBudgetId()
        );

        if (statistics.getRealBalance().compareTo(dto.getAmount()) < 0) {
            throw ApiException.with(ApiErrorCode.ERR_0014);
        }

        this.prepaymentRepository.saveAndFlush(prepayment);

        RecurringTransaction rTransaction = null;

        if (dto.getRecurringTransaction() != null) {
            rTransaction = this.recurringTransactionService.create(
                    dto.getRecurringTransaction());
        } else if (dto.getRecurringTransactionId() != null) {
            rTransaction = this.recurringTransactionService.findById(
                    dto.getRecurringTransactionId());
        }

        if (rTransaction != null) {
            this.recurringTransactionService.addPrepaymentId(prepayment.getId(), rTransaction);
        }

        return this.findById(
                prepayment.getId()
        );
    }
}
