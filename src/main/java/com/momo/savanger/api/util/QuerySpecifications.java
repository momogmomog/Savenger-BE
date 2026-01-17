package com.momo.savanger.api.util;

import static com.momo.savanger.api.util.QuerySpecificationUtils.betweenPredicate;
import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.function.SelfRenderingSqmAggregateFunction;
import org.hibernate.query.sqm.function.SelfRenderingSqmFunction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@Slf4j
public final class QuerySpecifications {

    private static final String COUNT_FUNCTION_NAME = "count";

    public static <T, V> Specification<T> equal(SingularAttribute<T, V> attribute, V value) {
        return (root, query, criteriaBuilder) -> equal(root.get(attribute), value, criteriaBuilder);
    }

    public static <T, V> Specification<T> equalIfPresent(SingularAttribute<T, V> attribute,
            V value) {
        if (value == null) {
            return Specification.where(null);
        }

        return equal(attribute, value);
    }

    public static <T> Predicate equal(Expression<T> field, T value, CriteriaBuilder cb) {
        return cb.equal(field, value);
    }

    public static <T, V> Specification<T> inIfPresent(SingularAttribute<T, V> attribute,
            Collection<V> values) {
        if (CollectionUtils.isEmpty(values)) {
            return Specification.where(null);
        }
        return in(attribute, values);
    }

    public static <T, V> Specification<T> in(SingularAttribute<T, V> attribute,
            Collection<V> values) {
        return (root, query, criteriaBuilder) -> root.get(attribute).in(values);
    }

    public static <T> Specification<T> containsIfPresent(SingularAttribute<T, String> attribute,
            String text) {
        if (text == null) {
            return Specification.where(null);
        }

        return contains(attribute, text);
    }

    public static <T> Specification<T> contains(SingularAttribute<T, String> attribute,
            String text) {
        return (root, query, criteriaBuilder)
                -> contains(attribute.getDeclaringType().getJavaType(), root.get(attribute), text)
                .toPredicate(root, query, criteriaBuilder);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Specification<T> contains(Class<T> type, Expression expression, String text) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.like(
                criteriaBuilder.lower(expression),
                "%" + StringUtil.lowerCase(text) + "%",
                '\\'
        );
    }

    public static <T, V extends Comparable<? super V>> Specification<T> between(
            SingularAttribute<T, V> attribute,
            BetweenQuery<V> query,
            boolean inclusive) {
        return (root, criteriaQuery, criteriaBuilder) -> betweenPredicate(
                attribute,
                query,
                criteriaBuilder,
                root,
                inclusive
        );
    }

    public static <T, V extends Comparable<? super V>> Specification<T> between(
            SingularAttribute<T, V> attribute,
            BetweenQuery<V> query) {
        return between(attribute, query, true);
    }

    /**
     * Creates a simple left join and applies between query on a field from the joined entity.
     *
     * @param join  - Field to join
     * @param attr  - Field from the joined entity to compare
     * @param query -
     * @param <T>   - Entity type
     * @param <J>   - Joined entity type
     * @param <V>   - Field value type
     * @return specification
     */
    public static <T, J, V extends Comparable<? super V>> Specification<T> betweenJoin(
            SingularAttribute<T, J> join,
            SingularAttribute<J, V> attr,
            BetweenQuery<V> query,
            boolean inclusive) {
        return (root, criteriaQuery, criteriaBuilder) -> betweenPredicate(
                attr,
                query,
                criteriaBuilder,
                getOrCreateJoin(root, join),
                inclusive
        );
    }

    public static <T, J, V extends Comparable<? super V>> Specification<T> betweenJoin(
            SingularAttribute<T, J> join,
            SingularAttribute<J, V> attr,
            BetweenQuery<V> query) {
        return betweenJoin(join, attr, query, true);
    }

    public static <T> Specification<T> sort(Class<T> entity, Expression<?> field,
            SortDirection direction) {
        return (root, query, criteriaBuilder) -> {
            final Selection<?> selection = query.getSelection();

            if (selection instanceof SelfRenderingSqmAggregateFunction) {
                var sqmFunction = (SelfRenderingSqmFunction<?>) selection;
                if (COUNT_FUNCTION_NAME.equalsIgnoreCase(sqmFunction.getFunctionName())) {
                    return criteriaBuilder.conjunction();
                } else {
                    log.warn("Unknown aggregation function {}", sqmFunction.getFunctionName());
                }
            }

//            if ((selection instanceof AggregationFunction)
//                    && AggregationFunction.COUNT.class.isAssignableFrom(selection.getClass())) {
//                return criteriaBuilder.conjunction();
//            }

            Order order = criteriaBuilder.asc(field);
            if (direction == SortDirection.DESC) {
                order = criteriaBuilder.desc(field);
            }

            query.orderBy(order);

            return criteriaBuilder.conjunction();
        };
    }
}
