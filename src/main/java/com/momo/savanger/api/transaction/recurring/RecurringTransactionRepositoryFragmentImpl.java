package com.momo.savanger.api.transaction.recurring;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class RecurringTransactionRepositoryFragmentImpl extends
        SpecificationExecutorImpl<RecurringTransaction, Long> implements
        RecurringTransactionRepositoryFragment {

    public RecurringTransactionRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, RecurringTransaction.class, Long.class, RecurringTransaction_.id);
    }
}
