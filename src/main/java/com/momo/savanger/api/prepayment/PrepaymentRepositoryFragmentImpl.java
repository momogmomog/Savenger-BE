package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class PrepaymentRepositoryFragmentImpl extends
        SpecificationExecutorImpl<Prepayment, Long> implements
        PrepaymentRepositoryFragment {

    public PrepaymentRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Prepayment.class, Long.class, Prepayment_.id);
    }
}
