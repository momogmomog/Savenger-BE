package com.momo.savanger.api.revision;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatisticsDto;
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

    @Override
    public Revision findById(Long id) {
        return this.revisionRepository.findById(id).orElseThrow(() ->
                ApiException.with(ApiErrorCode.ERR_0012));
    }

    @Override
    @Transactional
    public Revision create(CreateRevisionDto dto) {

        final Revision revision = this.revisionMapper.toRevision(dto);

        final BudgetStatisticsDto statisticDto = this.budgetService.getStatistics(
                dto.getBudgetId());

        revision.setRevisionDate(LocalDateTime.now());

        revision.setAutoRevise(false);

        revision.setBudgetCap(statisticDto.getBudget().getBudgetCap());
        revision.setBudgetStartDate(statisticDto.getBudget().getDateStarted());

        if (dto.getBalance() != null) {
            if (!Objects.equals(revision.getBalance(), statisticDto.getBalance())) {
                final BigDecimal compensationAmount = dto.getBalance()
                        .subtract(statisticDto.getBalance());
                this.transactionService.createCompensationTransaction(
                        dto.getBudgetId(),
                        compensationAmount
                );

                revision.setCompensationAmount(compensationAmount);
            }

            revision.setBalance(dto.getBalance());
        } else {
            revision.setBalance(statisticDto.getBalance());
        }

        revision.setEarningsAmount(statisticDto.getEarningsAmount());
        revision.setExpensesAmount(statisticDto.getExpensesAmount());

        this.revisionRepository.saveAndFlush(revision);

        this.budgetService.updateBudgetAfterRevision(revision.getBudgetId(), revision);

        this.transactionService.reviseTransactions(dto.getBudgetId());

        return this.findById(revision.getId());
    }
}
