package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final RecurringTransactionMapper recurringTransactionMapper;

    @Override
    public RecurringTransaction create(CreateRecurringTransactionDto dto) {
        RecurringTransaction recurringTransaction = this.recurringTransactionMapper.ToRecurringTransaction(
                dto);

        recurringTransaction.setCompleted(false);

        this.recurringTransactionRepository.saveAndFlush(recurringTransaction);

        return this.findById(recurringTransaction.getId());
    }

    @Override
    public RecurringTransaction findById(Long id) {
        return this.recurringTransactionRepository.findById(id)
                .orElseThrow(
                        () -> ApiException.with(ApiErrorCode.ERR_0017)
                );
    }

    @Override
    public void addPrepaymentId(Long prepaymentId, RecurringTransaction recurringTransaction) {
        recurringTransaction.setPrepaymentId(prepaymentId);
        this.recurringTransactionRepository.save(recurringTransaction);
    }


}
