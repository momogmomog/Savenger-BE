package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.recurringRule.RecurringRuleService;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.constants.EntityGraphs;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;

    private final RecurringTransactionMapper recurringTransactionMapper;

    private final RecurringRuleService recurringRuleService;

    private final TagService tagService;

    @Override
    @Transactional
    public RecurringTransaction create(CreateRecurringTransactionDto dto) {
        final RecurringTransaction recurringTransaction = this.recurringTransactionMapper.ToRecurringTransaction(
                dto);

        recurringTransaction.setCompleted(false);
        recurringTransaction.setNextDate(
                this.recurringRuleService.convertRecurringRuleToDate(
                        dto.getRecurringRule(),
                        LocalDateTime.now()
                )
        );

        if (!dto.getTagIds().isEmpty()) {
            recurringTransaction.setTags(this.tagService.findByBudgetAndIdContaining(
                    dto.getTagIds(),
                    dto.getBudgetId()
            ));
        }

        this.recurringTransactionRepository.saveAndFlush(recurringTransaction);

        return this.findById(recurringTransaction.getId());
    }

    @Override
    public RecurringTransaction findById(Long id) {
        return this.recurringTransactionRepository.findById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0016)
                );
    }

    @Override
    public RecurringTransaction findByIdFetchAll(Long id) {
        return this.findByIdIfExists(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0016)
                );
    }

    @Override
    public boolean recurringTransactionExists(Long recurringTransactionId, Long budgetId) {
        return this.recurringTransactionRepository.existsByIdAndBudgetId(recurringTransactionId,
                budgetId);
    }

    @Override
    public Optional<RecurringTransaction> findByIdIfExists(Long id) {
        final Specification<RecurringTransaction> specification = RecurringTransactionSpecifications
                .idEquals(id)
                .and(RecurringTransactionSpecifications.isCompleted(false));

        final List<RecurringTransaction> recurringTransactions = this.recurringTransactionRepository.findAll(
                specification, EntityGraphs.RECURRING_TRANSACTION_ALL
        );
        return recurringTransactions.stream().findFirst();
    }

    @Override
    @Transactional
    public void updateRecurringTransaction(RecurringTransaction recurringTransaction) {
        recurringTransaction.setUpdateDate(LocalDateTime.now());
        this.recurringTransactionRepository.save(recurringTransaction);
    }

    @Override
    public Page<RecurringTransaction> search(RecurringTransactionQuery query) {
        final Specification<RecurringTransaction> spec = RecurringTransactionSpecifications
                .sort(query.getSort())
                .and(RecurringTransactionSpecifications.budgetIdEquals(query.getBudgetId()))
                .and(RecurringTransactionSpecifications.typeEquals(query.getType()))
                .and(RecurringTransactionSpecifications.betweenNextDate(query.getNextDate()))
                .and(RecurringTransactionSpecifications.isAutoExecuted(query.getAutoExecute()))
                .and(RecurringTransactionSpecifications.betweenAmount(query.getAmount()))
                .and(RecurringTransactionSpecifications.prepaymentIdEquals(query.getPrepaymentId()))
                .and(RecurringTransactionSpecifications.isCompleted(query.getCompleted()))
                .and(RecurringTransactionSpecifications.categoryIdsIn(query.getCategoryIds()))
                .and(RecurringTransactionSpecifications.debtIdEquals(query.getDebtId()))
                .and(RecurringTransactionSpecifications.tagsContain(query.getTagIds()));

        return this.recurringTransactionRepository.findAll(
                spec,
                query.getPage(),
                EntityGraphs.RECURRING_TRANSACTION_TAGS
        );
    }
}
