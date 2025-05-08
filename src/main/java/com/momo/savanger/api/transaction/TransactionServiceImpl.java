package com.momo.savanger.api.transaction;

import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.user.User;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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
        return this.transactionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Transaction create(CreateTransactionDto dto, User user) {
        final Transaction transaction = this.transactionMapper.toTransaction(dto);

        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
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

}
