package com.momo.savanger.api.category;

import com.momo.savanger.api.util.QuerySpecifications;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecification {

    public static Specification<Category> idEquals(final Long id) {
        return QuerySpecifications.equal(Category_.id, id);
    }

    public static Specification<Category> budgetIdEquals(final Long budgetId) {
        return QuerySpecifications.equal(Category_.budgetId, budgetId);
    }
}
