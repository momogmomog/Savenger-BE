package com.momo.savanger.api.debt;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class DebtRepositoryFragmentImpl extends SpecificationExecutorImpl<Debt, Long> implements
        DebtRepositoryFragment {

    public DebtRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Debt.class, Long.class, Debt_.id);
    }
}
