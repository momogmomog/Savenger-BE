package com.momo.savanger.api.transaction;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.Tag_;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.QuerySpecifications;
import com.momo.savanger.api.util.ReflectionUtils;
import com.momo.savanger.api.util.SortQuery;
import jakarta.persistence.criteria.Join;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransactionSpecifications {

    public static Specification<Transaction> budgetIdEquals(final Long budgetId) {
        return QuerySpecifications.equal(Transaction_.budgetId, budgetId);
    }

    public static Specification<Transaction> betweenAmount(final BetweenQuery<BigDecimal> query) {
        return QuerySpecifications.between(Transaction_.amount, query);
    }

    public static Specification<Transaction> maybeContainsComment(final String comment) {
        return QuerySpecifications.containsIfPresent(Transaction_.comment, comment);
    }

    public static Specification<Transaction> maybeRevised(final Boolean revised) {
        return QuerySpecifications.equalIfPresent(Transaction_.revised, revised);
    }

    public static Specification<Transaction> categoryIdEquals(final Long categoryId) {
        return QuerySpecifications.equalIfPresent(Transaction_.categoryId, categoryId);
    }

    public static Specification<Transaction> typeEquals(final TransactionType type) {
        return QuerySpecifications.equalIfPresent(Transaction_.type, type);
    }

    public static Specification<Transaction> betweenDate(final BetweenQuery<LocalDateTime> query) {
        return QuerySpecifications.between(Transaction_.dateCreated, query);
    }

    public static Specification<Transaction> userIdEquals(final Long userId) {
        return QuerySpecifications.equalIfPresent(Transaction_.userId, userId);
    }

    public static Specification<Transaction> isLinkedToTag(final Long tagId) {
        if (tagId == null) {
            return Specification.where(null);
        }

        return (root, query, criteriaBuilder) -> {
            final Join<Transaction, Tag> join = getOrCreateJoin(root, Transaction_.tags);
            return criteriaBuilder.equal(join.get(Tag_.id), tagId);
        };
    }

    public static Specification<Transaction> sort(SortQuery sortQuery) {
        if (!ReflectionUtils.fieldExists(Transaction.class, sortQuery.getField())) {
            sortQuery.setField(Transaction_.ID);
        }

        return (root, query, criteriaBuilder)
                -> QuerySpecifications.sort(
                        Transaction.class,
                        root.get(sortQuery.getField()),
                        sortQuery.getDirection()
                )
                .toPredicate(root, query, criteriaBuilder);
    }

}
