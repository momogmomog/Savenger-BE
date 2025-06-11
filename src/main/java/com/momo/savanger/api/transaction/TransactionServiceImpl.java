package com.momo.savanger.api.transaction;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.user.User;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TagService tagService;

    private final BudgetService budgetService;

    @Override
    public Transaction findById(Long id) {
        return this.transactionRepository.findById(id).orElseThrow(() -> ApiException.with(
                ApiErrorCode.ERR_0010));
    }

    @Override
    @Transactional
    public Transaction create(CreateTransactionDto dto, User user) {
        final Transaction transaction = this.transactionMapper.toTransaction(dto);

        if (transaction.getDateCreated() == null) {
            transaction.setDateCreated(LocalDateTime.now());
        }

        transaction.setUserId(user.getId());
        transaction.setRevised(false);

        if (!dto.getTagIds().isEmpty()) {
            transaction.setTags(this.tagService.findByBudgetAndIdContaining(
                    dto.getTagIds(),
                    dto.getBudgetId()
            ));
        }

        this.transactionRepository.saveAndFlush(transaction);

        return this.findById(transaction.getId());
    }

    @Override
    public Page<Transaction> searchTransactions(TransactionSearchQuery query, User user) {
        final Specification<Transaction> specification = TransactionSpecifications
                .budgetIdEquals(query.getBudgetId())
                .and(TransactionSpecifications.sort(query.getSort()))
                .and(TransactionSpecifications.betweenAmount(query.getAmount()))
                .and(TransactionSpecifications.maybeRevised(query.getRevised()))
                .and(TransactionSpecifications.maybeContainsComment(query.getComment()))
                .and(TransactionSpecifications.betweenDate(query.getDateCreated()))
                .and(TransactionSpecifications.categoryIdEquals(query.getCategoryId()))
                .and(TransactionSpecifications.typeEquals(query.getType()))
                .and(TransactionSpecifications.userIdEquals(query.getUserId()))
                .and(TransactionSpecifications.isLinkedToTag(query.getTagId()));

        return this.transactionRepository.findAll(specification, query.getPage(), null);
    }

    @Override
    @Transactional
    public Transaction edit(Long id, EditTransactionDto dto) {

        final Transaction transaction = this.transactionMapper.mergeIntoTransaction(dto,
                this.findById(id));

        if (!dto.getTagIds().isEmpty()) {
            transaction.setTags(this.tagService.findByBudgetAndIdContaining(
                    dto.getTagIds(),
                    dto.getBudgetId()
            ));
        }

        this.transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public Boolean existsByIdAndRevisedFalse(Long id) {
        return this.transactionRepository.existsByIdAndRevisedFalse(id);
    }

    @Override
    public void deleteById(Long id) {

        this.transactionRepository.deleteById(id);

    }

    @Override
    public boolean canDeleteTransaction(Long transactionId, User user) {
        final Specification<Transaction> specification = TransactionSpecifications
                .idEquals(transactionId)
                .and(TransactionSpecifications.userIdEquals(user.getId()))
                .and(TransactionSpecifications.maybeRevised(false));

        return this.transactionRepository.exists(specification);
    }

    @Override
    public boolean canViewTransaction(Long transactionId, Long userId) {

        return this.transactionRepository.existOwnerOrParticipant(transactionId, userId);
    }

    @Override
    public void reviseTransactions(Long budgetId) {
        this.transactionRepository.setRevisedTrue(budgetId);
    }

    @Override
    public BigDecimal getExpensesAmount(Long budgetId) {
        BigDecimal expenses = this.transactionRepository.sumAmountByBudgetIdAndTypeOfNonRevised(
                budgetId,
                TransactionType.EXPENSE);

        if (expenses == null) {
            return BigDecimal.ZERO;
        }

        return expenses;
    }

    @Override
    public BigDecimal getEarningsAmount(Long budgetId) {
        BigDecimal earnings = this.transactionRepository.sumAmountByBudgetIdAndTypeOfNonRevised(
                budgetId,
                TransactionType.INCOME);

        if (earnings == null) {
            return BigDecimal.ZERO;
        }

        return earnings;
    }

    @Override
    public BigDecimal getBalance(Long budgetId) {
        final Budget budget = this.budgetService.findById(budgetId);

        BigDecimal expensesAmount = this.getExpensesAmount(budgetId);
        BigDecimal earningsAmount = this.getEarningsAmount(budgetId);

        return (earningsAmount.add(budget.getBalance())).subtract(expensesAmount);
    }
}
