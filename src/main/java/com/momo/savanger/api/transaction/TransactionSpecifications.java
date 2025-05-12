package com.momo.savanger.api.transaction;

import com.momo.savanger.api.util.QuerySpecifications;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionSpecifications {

    public static Specification<Transaction> budgetIdEquals(final Long budgetId) {
        return QuerySpecifications.equal(Transaction_.budgetId, budgetId);
    }
}
