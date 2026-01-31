package com.momo.savanger.api.transfer;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.Budget_;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.User_;
import com.momo.savanger.api.util.QuerySpecifications;
import jakarta.persistence.criteria.Join;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferSpecifications {

    public static Specification<Transfer> idEquals(final Long id) {
        return QuerySpecifications.equal(Transfer_.id, id);
    }

    public static Specification<Transfer> sourceBudgetIdEquals(final Long id) {
        return QuerySpecifications.equal(Transfer_.sourceBudgetId, id);
    }

    public static Specification<Transfer> receiverBudgetIdEquals(final Long id) {
        return QuerySpecifications.equal(Transfer_.receiverBudgetId, id);
    }

    public static Specification<Transfer> isActive(final Boolean active) {
        return QuerySpecifications.equalIfPresent(Transfer_.active, active);
    }

    public static Specification<Transfer> receivedBudgetIdIn(
            final Collection<Long> receivedBudgetIds) {
        return QuerySpecifications.inIfPresent(Transfer_.receiverBudgetId, receivedBudgetIds);
    }

    public static Specification<Transfer> canAccessReceiverBudget(final Long userId) {
        if (userId == null) {
            return Specification.not(null);
        }

        return (root, query, criteriaBuilder) -> {
            final Join<Transfer, Budget> budgetJoin = getOrCreateJoin(root,
                    Transfer_.receiverBudget);
            final Join<Budget, User> participantsJoin = getOrCreateJoin(budgetJoin,
                    Budget_.participants);

            return criteriaBuilder.and(
                    criteriaBuilder.equal(budgetJoin.get(Budget_.active), true),
                    criteriaBuilder.or(
                            criteriaBuilder.equal(budgetJoin.get(Budget_.ownerId), userId),
                            criteriaBuilder.equal(participantsJoin.get(User_.id), userId)
                    )
            );
        };
    }


}
