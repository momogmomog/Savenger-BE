package com.momo.savanger.api.debt;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.Budget_;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.User_;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.QuerySpecifications;
import com.momo.savanger.api.util.ReflectionUtils;
import com.momo.savanger.api.util.SortQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebtSpecifications {

    public static Specification<Debt> idEquals(final Long id) {
        return QuerySpecifications.equal(Debt_.id, id);
    }

    public static Specification<Debt> receiverBudgetIdEquals(final Long receiverBudgetId) {
        return QuerySpecifications.equalIfPresent(Debt_.receiverBudgetId, receiverBudgetId);
    }

    public static Specification<Debt> lenderBudgetIdEquals(final Long lenderBudgetId) {
        return QuerySpecifications.equalIfPresent(Debt_.lenderBudgetId, lenderBudgetId);
    }

    public static Specification<Debt> receiverNameContains(final String receiverName) {
        return QuerySpecifications.containsJoin(
                Debt_.receiverBudget,
                Budget_.budgetName,
                receiverName
        );
    }

    public static Specification<Debt> lenderNameContains(final String receiverName) {
        return QuerySpecifications.containsJoin(
                Debt_.lenderBudget,
                Budget_.budgetName,
                receiverName
        );
    }

    public static Specification<Debt> budgetIdIn(final Collection<Long> budgetIds) {
        if (CollectionUtils.isEmpty(budgetIds)) {
            return Specification.where(null);
        }

        return QuerySpecifications.in(Debt_.receiverBudgetId, budgetIds)
                .or(QuerySpecifications.in(Debt_.lenderBudgetId, budgetIds));
    }

    public static Specification<Debt> betweenAmount(final BetweenQuery<BigDecimal> query) {
        return QuerySpecifications.between(Debt_.amount, query);
    }

    public static Specification<Debt> canAccessReceiverBudget(final Long userId) {
        return canAccessBudget(Debt_.receiverBudget, userId);
    }

    public static Specification<Debt> canAccessLenderBudget(final Long userId) {
        return canAccessBudget(Debt_.lenderBudget, userId);
    }

    private static Specification<Debt> canAccessBudget(
            final SingularAttribute<Debt, Budget> budgetAttribute,
            final Long userId) {
        if (userId == null) {
            return Specification.not(null);
        }

        return (root, query, criteriaBuilder) -> {
            final Join<Debt, Budget> budgetJoin = getOrCreateJoin(root, budgetAttribute);
            final Join<Budget, User> participantsJoin = getOrCreateJoin(
                    budgetJoin,
                    Budget_.participants
            );

            return criteriaBuilder.and(
                    criteriaBuilder.equal(budgetJoin.get(Budget_.active), true),
                    criteriaBuilder.or(
                            criteriaBuilder.equal(budgetJoin.get(Budget_.ownerId), userId),
                            criteriaBuilder.equal(participantsJoin.get(User_.id), userId)
                    )
            );
        };
    }

    public static Specification<Debt> sort(SortQuery sortQuery) {
        if (!ReflectionUtils.fieldExists(Debt.class, sortQuery.getField())) {
            sortQuery.setField(Debt_.ID);
        }

        return (root, query, criteriaBuilder)
                -> QuerySpecifications.sort(
                        Debt.class,
                        root.get(sortQuery.getField()),
                        sortQuery.getDirection()
                )
                .toPredicate(root, query, criteriaBuilder);
    }
}
