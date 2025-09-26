package com.momo.savanger.api.prepayment;

import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.QuerySpecifications;
import com.momo.savanger.api.util.ReflectionUtils;
import com.momo.savanger.api.util.SortQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrepaymentSpecifications {

    public static Specification<Prepayment> idEquals(final Long id) {
        return QuerySpecifications.equal(Prepayment_.id, id);
    }

    public static Specification<Prepayment> betweenAmount(
            final BetweenQuery<BigDecimal> query) {
        return QuerySpecifications.between(Prepayment_.amount, query);
    }

    public static Specification<Prepayment> nameEquals(final String name) {
        return QuerySpecifications.equalIfPresent(Prepayment_.name, name);
    }

    public static Specification<Prepayment> betweenPaidUntil(
            final BetweenQuery<LocalDateTime> query) {
        return QuerySpecifications.between(Prepayment_.paidUntil, query);
    }

    public static Specification<Prepayment> isCompleted(final boolean isCompleted) {
        return QuerySpecifications.equalIfPresent(Prepayment_.completed, isCompleted);
    }

    public static Specification<Prepayment> betweenRemainingAmount(
            final BetweenQuery<BigDecimal> query) {
        return QuerySpecifications.between(Prepayment_.remainingAmount, query);
    }

    public static Specification<Prepayment> budgetIdEquals(final Long budgetId) {
        return QuerySpecifications.equalIfPresent(Prepayment_.budgetId, budgetId);
    }


    public static Specification<Prepayment> sort(SortQuery sortQuery) {
        if (!ReflectionUtils.fieldExists(Prepayment.class, sortQuery.getField())) {
            sortQuery.setField(Prepayment_.ID);
        }

        return (root, query, criteriaBuilder)
                -> QuerySpecifications.sort(
                        Prepayment.class,
                        root.get(sortQuery.getField()),
                        sortQuery.getDirection()
                )
                .toPredicate(root, query, criteriaBuilder);
    }

}
