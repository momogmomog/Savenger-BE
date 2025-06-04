package com.momo.savanger.api.transaction;

import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.user.User;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
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
    public Transaction edit(Long id, EditTransactionDto dto) {

        Transaction transaction = this.findById(id);

        transaction = this.mergeTransactions(transaction, dto);

        if (!dto.getTagIds().isEmpty()) {
            transaction.setTags(this.tagService.findByBudgetAndIdContaining(
                    dto.getTagIds(),
                    dto.getBudgetId()
            ));
        }

        this.transactionRepository.save(transaction);

        return transaction;
    }

    private Transaction mergeTransactions(
            Transaction transaction, EditTransactionDto dto) {

        if (dto.getType() != null) {
            transaction.setType(dto.getType());
        }

        if (dto.getAmount() != null) {
            transaction.setAmount(dto.getAmount());
        }

        if (dto.getDateCreated() != null) {
            transaction.setDateCreated(dto.getDateCreated());
        }

        if (dto.getComment() != null) {
            transaction.setComment(dto.getComment());
        }

        if (dto.getCategoryId() != null) {
            transaction.setCategoryId(dto.getCategoryId());
        }

        if (dto.getBudgetId() != null) {
            transaction.setBudgetId(dto.getBudgetId());
        }

        return transaction;
    }
}
