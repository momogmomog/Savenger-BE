package com.momo.savanger.api.transaction;

import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
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
    public PagedModel<Transaction> searchTransactions(TransactionSearchQuery query) {
        final Specification<Transaction> specification = TransactionSpecifications
                .budgetIdEquals(query.getBudgetId())
                .and(TransactionSpecifications.sort(query.getSort()))
                .and(TransactionSpecifications.betweenAmount(query.getAmount()))
                .and(TransactionSpecifications.maybeRevised(query.getRevised()))
                .and(TransactionSpecifications.maybeContainsComment(query.getComment()));
        // TODO: add all fields

        return new PagedModel<>(
                this.transactionRepository.findAll(specification, query.getPage(), null)
        );
    }
}
