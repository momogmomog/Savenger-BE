package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.constants.EntityGraphs;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final RecurringTransactionMapper recurringTransactionMapper;

    @Override
    @Transactional
    public RecurringTransaction create(CreateRecurringTransactionDto dto) {
        final RecurringTransaction recurringTransaction = this.recurringTransactionMapper.ToRecurringTransaction(
                dto);

        recurringTransaction.setCompleted(false);

        //While implementing RRule
        recurringTransaction.setNextDate(LocalDateTime.now());

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
    @Transactional
    public void addPrepaymentId(Long prepaymentId, RecurringTransaction recurringTransaction) {
        recurringTransaction.setPrepaymentId(prepaymentId);
        this.recurringTransactionRepository.save(recurringTransaction);
    }

    @Override
    public Boolean isRecurringTransactionValid(Long recurringTransactionId) {
        return this.findByIdIfExists(recurringTransactionId).isPresent();
    }

    @Override
    public Optional<RecurringTransaction> findByIdIfExists(Long id) {
        final Specification<RecurringTransaction> specification = RecurringTransactionSpecifications.idEquals(
                        id)
                .and(RecurringTransactionSpecifications.isCompleted(false));

        final List<RecurringTransaction> recurringTransactions = this.recurringTransactionRepository.findAll(
                specification, EntityGraphs.RECURRING_TRANSACTION_ALL
        );
        return recurringTransactions.stream().findFirst();
    }
}
