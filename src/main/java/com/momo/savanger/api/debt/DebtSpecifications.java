package com.momo.savanger.api.debt;

import com.momo.savanger.api.util.QuerySpecifications;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebtSpecifications {

    public static Specification<Debt> idEquals(final Long id) {
        return QuerySpecifications.equal(Debt_.id, id);
    }

    public static Specification<Debt> receiverBudgetIdEquals(final Long receiverBudgetId) {
        return QuerySpecifications.equal(Debt_.receiverBudgetId, receiverBudgetId);
    }

    public static Specification<Debt> lenderBudgetIdEquals(final Long lenderBudgetId) {
        return QuerySpecifications.equal(Debt_.lenderBudgetId, lenderBudgetId);
    }
}
