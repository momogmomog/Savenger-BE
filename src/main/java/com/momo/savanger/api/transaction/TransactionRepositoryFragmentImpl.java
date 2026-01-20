package com.momo.savanger.api.transaction;

import static com.momo.savanger.api.util.QuerySpecificationUtils.getOrCreateJoin;

import com.momo.savanger.api.util.SpecificationExecutorImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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

        // TODO: use the constant class.
        query.select(join.get("tagId"));
        query.where(specification.toPredicate(root, query, criteriaBuilder));

        query.distinct(true);

        final TypedQuery<Long> typedQuery = this.entityManager.createQuery(query);

        return typedQuery.getResultList();
    }

    @Override
    public BigDecimal sum(Specification<Transaction> specification) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final CriteriaQuery<BigDecimal> query = criteriaBuilder.createQuery(BigDecimal.class);
        final Root<Transaction> root = query.from(this.entityType);

        query.select(criteriaBuilder.sum(root.get(Transaction_.amount)));
        query.where(specification.toPredicate(root, query, criteriaBuilder));

        final TypedQuery<BigDecimal> typedQuery = this.entityManager.createQuery(query);

        return typedQuery.getSingleResult();
    }
}
