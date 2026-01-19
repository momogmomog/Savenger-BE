package com.momo.savanger.api.transfer;

import com.momo.savanger.api.util.QuerySpecifications;
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

}
