package com.momo.savanger.api.transaction;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class TransactionRepositoryFragmentImpl extends
        SpecificationExecutorImpl<Transaction, Long> implements
        TransactionRepositoryFragment {

    public TransactionRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Transaction.class, Long.class, Transaction_.id);
    }
}
