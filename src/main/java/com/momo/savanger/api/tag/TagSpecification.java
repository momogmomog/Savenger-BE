package com.momo.savanger.api.tag;

import com.momo.savanger.api.util.QuerySpecifications;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class TagSpecification {

    public static Specification<Tag> idEquals(final Long id) {
        return QuerySpecifications.equal(Tag_.id, id);
    }

    public static Specification<Tag> idIn(final List<Long> ids) {
        return (root, query, criteriaBuilder) -> root.get(Tag_.id).in(ids);
    }

    public static Specification<Tag> budgetIdEquals(final Long budgetId) {
        return QuerySpecifications.equal(Tag_.budgetId, budgetId);
    }
}
