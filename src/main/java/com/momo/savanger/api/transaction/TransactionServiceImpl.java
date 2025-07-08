package com.momo.savanger.api.transaction;

import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
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

    @Override
    public Transaction findById(Long id) {
        return this.transactionRepository.findById(id).orElseThrow(() -> ApiException.with(
                ApiErrorCode.ERR_0010));
    }

    @Override
    @Transactional
    public Transaction create(CreateTransactionDto dto, Long userId) {
        final Transaction transaction = this.transactionMapper.toTransaction(dto);

        if (transaction.getDateCreated() == null) {
            transaction.setDateCreated(LocalDateTime.now());
        }

        transaction.setUserId(userId);
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
    @Transactional
    public void createDebtTransactions(Debt debt, BigDecimal amount) {
        final Long userId = SecurityUtils.getCurrentUser().getId();

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.EXPENSE,
                        debt.getLenderBudgetId()), userId
        );

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.INCOME,
                        debt.getReceiverBudgetId()), userId
        );
    }

    @Override
    @Transactional
    public void payDebtTransaction(Debt debt, BigDecimal amount) {
        final Long userId = SecurityUtils.getCurrentUser().getId();

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.EXPENSE,
                        debt.getReceiverBudgetId()), userId
        );

        this.create(
                this.createTransactionDto(amount, debt.getId(), TransactionType.INCOME,
                        debt.getLenderBudgetId()), userId
        );
    }

    private CreateTransactionDto createTransactionDto(BigDecimal amount, Long debtId,
            TransactionType transactionType, Long budgetId) {

        return CreateTransactionDto.debtDto(amount,
                debtId,
                transactionType,
                budgetId
        );
    }

    @Override
    @Transactional
    public Transaction createCompensationTransaction(Long budgetId, BigDecimal amount) {

        return this.create(CreateTransactionDto.compensateDto(amount, budgetId), null);

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

        return this.getSumAmount(budgetId, TransactionType.EXPENSE);
    }

    @Override
    public BigDecimal getEarningsAmount(Long budgetId) {

        return this.getSumAmount(budgetId, TransactionType.INCOME);
    }


    @Override
    public BigDecimal getDebtLendedAmount(Long budgetId) {
        return Objects.requireNonNullElse(
                this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(budgetId,
                        TransactionType.EXPENSE), BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getDebtReceivedAmount(Long budgetId) {
        return Objects.requireNonNullElse(
                this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(budgetId,
                        TransactionType.INCOME), BigDecimal.ZERO);
    }

    private BigDecimal getSumAmount(Long budgetId, TransactionType type) {

        return Objects.requireNonNullElse(
                this.transactionRepository.sumAmountByBudgetIdAndTypeOfNonRevised(budgetId,
                        type), BigDecimal.ZERO);
    }
}
