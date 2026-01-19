package com.momo.savanger.api.transaction;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionRepositoryFragmentImpl extends
        SpecificationExecutorImpl<Transaction, Long> implements
        TransactionRepositoryFragment {

    public TransactionRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Transaction.class, Long.class, Transaction_.id);
    }

    @Override
    @Transactional
    public List<Long> getCategoryIds(Specification<Transaction> specification) {
        return List.of();
    }

    @Override
    @Transactional
    public List<Long> getTagIds(Specification<Transaction> specification) {
        return List.of();
    }
}
