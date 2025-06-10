package com.momo.savanger.api.revision;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.transaction.TransactionService;
import java.time.LocalDateTime;
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
        return this.revisionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Revision create(CreateRevisionDto dto) {
        Budget budget = budgetService.findById(dto.getBudgetId());

        Revision revision = this.revisionMapper.toRevision(dto);

        revision.setRevisionDate(LocalDateTime.now());

        revision.setAutoRevise(budget.getAutoRevise());
        revision.setBudgetCap(budget.getBudgetCap());
        revision.setBudgetStartDate(budget.getDateStarted());

        if (dto.getBalance() != null) {
            revision.setBalance(dto.getBalance());
        } else {
            revision.setBalance(this.budgetService.balance(dto.getBudgetId()));
        }

        revision.setEarningsAmount(this.budgetService.earningsAmount(dto.getBudgetId()));
        revision.setExpensesAmount(this.budgetService.expensesAmount(dto.getBudgetId()));

        this.revisionRepository.saveAndFlush(revision);

        this.transactionService.revisedTransactions(dto.getBudgetId());

        return this.findById(revision.getId());
    }
}
