package com.momo.savanger.api.category;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class CategoryRepositoryFragmentImpl extends
        SpecificationExecutorImpl<Category, Long> implements
        CategoryRepositoryFragment {

    public CategoryRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Category.class, Long.class, Category_.id);
    }
}
