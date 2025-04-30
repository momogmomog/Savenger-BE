package com.momo.savanger.api.budget;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class BudgetRepositoryFragmentImpl extends SpecificationExecutorImpl<Budget, Long> implements
        BudgetRepositoryFragment {

    public BudgetRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Budget.class, Long.class, Budget_.id);
    }
}
