package com.momo.savanger.api.budget;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.user.User_;
import com.momo.savanger.api.util.QuerySpecifications;
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
}
