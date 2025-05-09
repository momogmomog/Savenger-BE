package com.momo.savanger.api.tag;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class TagRepositoryFragmentImpl extends
        SpecificationExecutorImpl<Tag, Long> implements
        TagRepositoryFragment {

    public TagRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Tag.class, Long.class, Tag_.id);
    }
}
