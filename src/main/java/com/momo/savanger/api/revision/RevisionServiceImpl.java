package com.momo.savanger.api.revision;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RevisionServiceImpl implements RevisionService {

    private final RevisionRepository revisionRepository;

    private final RevisionMapper revisionMapper;

    private final BudgetService budgetService;

    private final TransactionService transactionService;

    private final PrepaymentService prepaymentService;

    @Override
    public Revision findById(Long id) {
        return this.revisionRepository.findById(id).orElseThrow(() ->
                ApiException.with(ApiErrorCode.ERR_0012));
    }

    @Override
    @Transactional
    public Revision create(CreateRevisionDto dto) {

        final Revision revision = this.revisionMapper.toRevision(dto);

        final BudgetStatistics statistics = this.budgetService.getStatistics(
                dto.getBudgetId());

        final Budget budget = statistics.getBudget();

        revision.setRevisionDate(LocalDateTime.now());

        revision.setAutoRevise(false);

        revision.setBudgetCap(budget.getBudgetCap());
        revision.setBudgetStartDate(budget.getDateStarted());

        if (dto.getBalance() != null) {
            if (!Objects.equals(revision.getBalance(), statistics.getBalance())) {
                final BigDecimal compensationAmount = dto.getBalance()
                        .subtract(statistics.getBalance());
                this.transactionService.createCompensationTransaction(
                        dto.getBudgetId(),
                        compensationAmount
                );

                revision.setCompensationAmount(compensationAmount);
            }

            revision.setBalance(dto.getBalance());
        } else {
            revision.setBalance(statistics.getBalance());
        }

        // TODO: Save remaining prepayment data here too
        final BigDecimal prepaymentsAmount = this.prepaymentService
                .getRemainingPrepaymentAmountSumByBudgetId(budget.getId());

        revision.setEarningsAmount(statistics.getEarningsAmount());
        revision.setExpensesAmount(statistics.getExpensesAmount());
        revision.setDebtLendedAmount(statistics.getDebtLendedAmount());
        revision.setDebtReceivedAmount(statistics.getDebtReceivedAmount());

        this.revisionRepository.saveAndFlush(revision);

        this.budgetService.updateBudgetAfterRevision(revision.getBudgetId(), revision);

        this.transactionService.reviseTransactions(dto.getBudgetId());

        return this.findById(revision.getId());
    }
}
