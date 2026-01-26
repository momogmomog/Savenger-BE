package com.momo.savanger.api.transfer;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class TransferRepositoryFragmentImpl extends
        SpecificationExecutorImpl<Transfer, Long> implements TransferRepositoryFragment {

    public TransferRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Transfer.class, Long.class, Transfer_.id);
    }
}
