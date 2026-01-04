package com.momo.savanger.api.tag;

import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.QuerySpecifications;
import com.momo.savanger.api.util.ReflectionUtils;
import com.momo.savanger.api.util.SortQuery;
import java.math.BigDecimal;
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

    public static Specification<Tag> nameContains(final String name) {
        return QuerySpecifications.containsIfPresent(Tag_.tagName, name);
    }

    public static Specification<Tag> capBetween(final BetweenQuery<BigDecimal> betweenQuery) {
        return QuerySpecifications.between(Tag_.budgetCap, betweenQuery);
    }

    public static Specification<Tag> sort(SortQuery sortQuery) {
        if (!ReflectionUtils.fieldExists(Tag.class, sortQuery.getField())) {
            sortQuery.setField(Tag_.ID);
        }

        return (root, query, criteriaBuilder)
                -> QuerySpecifications.sort(
                        Tag.class,
                        root.get(sortQuery.getField()),
                        sortQuery.getDirection()
                )
                .toPredicate(root, query, criteriaBuilder);
    }
}
