package com.momo.savanger.api.budget;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.user.User_;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.QuerySpecifications;
import com.momo.savanger.api.util.ReflectionUtils;
import com.momo.savanger.api.util.SortQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class BudgetSpecifications {

    public static Specification<Budget> idEquals(final Long id) {
        return QuerySpecifications.equal(Budget_.id, id);
    }

    public static Specification<Budget> ownerIdEquals(final Long ownerId) {
        return QuerySpecifications.equal(Budget_.ownerId, ownerId);
    }

    public static Specification<Budget> isActive() {
        return QuerySpecifications.equal(Budget_.active, true);
    }

    public static Specification<Budget> containsParticipant(final Long userId) {
        return (root, query, cb) -> cb.equal(
                getOrCreateJoin(root, Budget_.participants).get(User_.id),
                userId
        );
    }

    public static Specification<Budget> isActive(final Boolean active) {
        return QuerySpecifications.equalIfPresent(Budget_.active, active);
    }

    public static Specification<Budget> isAutoRevise(final Boolean autoRevise) {
        return QuerySpecifications.equalIfPresent(Budget_.autoRevise, autoRevise);
    }

    public static Specification<Budget> budgetNameContains(final String name) {
        return QuerySpecifications.containsIfPresent(Budget_.budgetName, name);
    }

    public static Specification<Budget> betweenDateStarted(final BetweenQuery<LocalDateTime> date) {
        return QuerySpecifications.between(Budget_.dateStarted, date);
    }

    public static Specification<Budget> betweenDueDate(final BetweenQuery<LocalDateTime> date) {
        return QuerySpecifications.between(Budget_.dueDate, date);
    }

    public static Specification<Budget> betweenBalance(final BetweenQuery<BigDecimal> balance) {
        return QuerySpecifications.between(Budget_.balance, balance);
    }

    public static Specification<Budget> betweenBudgetCap(final BetweenQuery<BigDecimal> budgetCap) {
        return QuerySpecifications.between(Budget_.budgetCap, budgetCap);
    }

    public static Specification<Budget> idNotIn(List<Long> budgetIds) {
        if (budgetIds == null || budgetIds.isEmpty()) {
            return Specification.where(null);
        }

        return (root, query, cb) -> cb.not(root.get(Budget_.id).in(budgetIds));
    }

    public static Specification<Budget> sort(SortQuery sortQuery) {
        if (!ReflectionUtils.fieldExists(Budget.class, sortQuery.getField())) {
            sortQuery.setField(Budget_.ID);
        }

        return (root, query, criteriaBuilder)
                -> QuerySpecifications.sort(
                        Budget.class,
                        root.get(sortQuery.getField()),
                        sortQuery.getDirection()
                )
                .toPredicate(root, query, criteriaBuilder);
    }
}
