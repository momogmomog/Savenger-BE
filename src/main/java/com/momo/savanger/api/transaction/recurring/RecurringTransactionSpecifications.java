package com.momo.savanger.api.transaction.recurring;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.Tag_;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.QuerySpecifications;
import com.momo.savanger.api.util.ReflectionUtils;
import com.momo.savanger.api.util.SortQuery;
import jakarta.persistence.criteria.Join;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecurringTransactionSpecifications {

    public static Specification<RecurringTransaction> idEquals(final Long id) {
        return QuerySpecifications.equal(RecurringTransaction_.id, id);
    }

    public static Specification<RecurringTransaction> typeEquals(final TransactionType type) {
        return QuerySpecifications.equalIfPresent(RecurringTransaction_.type, type);
    }

    public static Specification<RecurringTransaction> betweenNextDate(
            final BetweenQuery<LocalDateTime> query) {
        return QuerySpecifications.between(RecurringTransaction_.nextDate, query);
    }

    public static Specification<RecurringTransaction> isAutoExecuted(final Boolean isAutoExecuted) {
        return QuerySpecifications.equalIfPresent(
                RecurringTransaction_.autoExecute,
                isAutoExecuted
        );
    }

    public static Specification<RecurringTransaction> betweenAmount(
            final BetweenQuery<BigDecimal> query) {
        return QuerySpecifications.between(RecurringTransaction_.amount, query);
    }

    public static Specification<RecurringTransaction> prepaymentIdEquals(final Long prepaymentId) {
        return QuerySpecifications.equalIfPresent(RecurringTransaction_.prepaymentId, prepaymentId);
    }

    public static Specification<RecurringTransaction> isCompleted(final Boolean isCompleted) {
        return QuerySpecifications.equalIfPresent(RecurringTransaction_.completed, isCompleted);
    }

    public static Specification<RecurringTransaction> categoryIdEquals(final Long categoryId) {
        return QuerySpecifications.equalIfPresent(RecurringTransaction_.categoryId, categoryId);
    }

    public static Specification<RecurringTransaction> categoryIdsIn(final List<Long> categoryIds) {
        return QuerySpecifications.inIfPresent(RecurringTransaction_.categoryId, categoryIds);
    }

    public static Specification<RecurringTransaction> budgetIdEquals(final Long budgetId) {
        return QuerySpecifications.equal(RecurringTransaction_.budgetId, budgetId);
    }

    public static Specification<RecurringTransaction> debtIdEquals(final Long debtId) {
        return QuerySpecifications.equalIfPresent(RecurringTransaction_.debtId, debtId);
    }

    public static Specification<RecurringTransaction> isLinkedToTag(final Long tagId) {
        if (tagId == null) {
            return Specification.where(null);
        }

        return (root, query, criteriaBuilder) -> {
            final Join<RecurringTransaction, Tag> join = getOrCreateJoin(root,
                    RecurringTransaction_.tags);
            return criteriaBuilder.equal(join.get(Tag_.id), tagId);
        };
    }

    public static Specification<RecurringTransaction> tagsContain(final Collection<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return Specification.where(null);
        }

        return (root, query, criteriaBuilder) -> {
            final Join<RecurringTransaction, Tag> join = getOrCreateJoin(root, RecurringTransaction_.tags);
            return join.get(Tag_.id).in(tagIds);
        };
    }

    public static Specification<RecurringTransaction> sort(SortQuery sortQuery) {
        if (!ReflectionUtils.fieldExists(RecurringTransaction_.class, sortQuery.getField())) {
            sortQuery.setField(RecurringTransaction_.ID);
        }

        return (root, query, criteriaBuilder)
                -> QuerySpecifications.sort(
                        RecurringTransaction.class,
                        root.get(sortQuery.getField()),
                        sortQuery.getDirection()
                )
                .toPredicate(root, query, criteriaBuilder);
    }

}
