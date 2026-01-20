package com.momo.savanger.api.category;

import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.QuerySpecifications;
import com.momo.savanger.api.util.ReflectionUtils;
import com.momo.savanger.api.util.SortQuery;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecification {

    public static Specification<Category> idEquals(final Long id) {
        return QuerySpecifications.equal(Category_.id, id);
    }

    public static Specification<Category> idIn(final List<Long> ids) {
        return QuerySpecifications.in(Category_.id, ids);
    }

    public static Specification<Category> budgetIdEquals(final Long budgetId) {
        return QuerySpecifications.equal(Category_.budgetId, budgetId);
    }

    public static Specification<Category> nameContains(final String name) {
        return QuerySpecifications.containsIfPresent(Category_.categoryName, name);
    }

    public static Specification<Category> capBetween(final BetweenQuery<BigDecimal> betweenQuery) {
        return QuerySpecifications.between(Category_.budgetCap, betweenQuery);
    }

    public static Specification<Category> sort(SortQuery sortQuery) {
        if (!ReflectionUtils.fieldExists(Category.class, sortQuery.getField())) {
            sortQuery.setField(Category_.ID);
        }

        return (root, query, criteriaBuilder)
                -> QuerySpecifications.sort(
                        Category.class,
                        root.get(sortQuery.getField()),
                        sortQuery.getDirection()
                )
                .toPredicate(root, query, criteriaBuilder);
    }
}
