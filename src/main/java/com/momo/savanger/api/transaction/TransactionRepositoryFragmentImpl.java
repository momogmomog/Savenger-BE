package com.momo.savanger.api.transaction;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.tag.Tag_;
import com.momo.savanger.api.transaction.dto.TransactionBasicDto;
import com.momo.savanger.api.transaction.dto.TransactionSumAndCount;
import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionRepositoryFragmentImpl extends
        SpecificationExecutorImpl<Transaction, Long> implements
        TransactionRepositoryFragment {

    public TransactionRepositoryFragmentImpl(EntityManager entityManager) {
        super(entityManager, Transaction.class, Long.class, Transaction_.id);
    }

    @Override
    @Transactional
    public List<Long> getCategoryIds(Specification<Transaction> specification) {
        return super.selectXFindAllDistinct(specification, Transaction_.categoryId);
    }

    @Override
    @Transactional
    public List<Long> getTagIds(Specification<Transaction> specification) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        final Root<Transaction> root = query.from(this.entityType);
        final var join = getOrCreateJoin(root, Transaction_.tags);

        query.select(join.get(Tag_.id));
        query.where(specification.toPredicate(root, query, criteriaBuilder));

        query.distinct(true);

        final TypedQuery<Long> typedQuery = this.entityManager.createQuery(query);

        return typedQuery.getResultList();
    }

    @Override
    @Transactional
    public TransactionSumAndCount sumAndCount(Specification<Transaction> specification) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<TransactionBasicDto> query = cb.createQuery(
                TransactionBasicDto.class);
        final Root<Transaction> root = query.from(Transaction.class);

        final Subquery<Long> subquery = query.subquery(Long.class);
        final Root<Transaction> subRoot = subquery.from(Transaction.class);
        subquery.select(subRoot.get(Transaction_.id));
        // No need to distinct as the "in" operator on the outer query would do that anyways!
//        subquery.distinct(true);
        subquery.where(specification.toPredicate(subRoot, query, cb));

        query.where(root.get(Transaction_.id).in(subquery));

        query.select(cb.construct(
                TransactionBasicDto.class,
                root.get(Transaction_.id),
                root.get(Transaction_.amount),
                root.get(Transaction_.type)
        ));

        final List<TransactionBasicDto> transactions = entityManager.createQuery(query)
                .getResultList();

        final BigDecimal sum = transactions.stream()
                .map(TransactionBasicDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TransactionSumAndCount(sum, (long) transactions.size(), transactions);
    }
}
