package com.momo.savanger.api.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
public class SpecificationExecutorImpl<T, ID> implements SpecificationExecutor<T, ID> {

    protected static final String HIBERNATE_HINT_ENTITY_GRAPH = "jakarta.persistence.fetchgraph";

    protected final EntityManager entityManager;

    protected final Class<T> entityType;

    protected final Class<ID> entityIdType;

    protected final SingularAttribute<T, ID> entityIdField;

    @Override
    @Transactional
    public boolean exists(Specification<T> specification) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        final Root<T> root = query.from(this.entityType);
        query.select(criteriaBuilder.count(root));

        query.where(specification.toPredicate(root, query, criteriaBuilder));

        return this.entityManager.createQuery(query).getResultStream().findFirst().orElse(0L) > 0;
    }

    @Override
    @Transactional
    public Page<T> findAll(Specification<T> specification,
            PageQuery pageQuery,
            @Nullable String entityGraph) {
        final Page<ID> page = this.selectIdFindAll(specification, pageQuery);
        if (page.isEmpty()) {
            return new PageImpl<>(List.of());
        }

        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final CriteriaQuery<T> query = criteriaBuilder.createQuery(this.entityType);
        final Root<T> root = query.from(this.entityType);

        final CriteriaQuery<T> queryForOrder = criteriaBuilder.createQuery(this.entityType);
        final List<Order> orderList = queryForOrder
                .where(specification.toPredicate(root, queryForOrder, criteriaBuilder))
                .getOrderList();

        query.where(root.get(this.entityIdField).in(page.getContent()));
        root.getJoins().clear();
        query.orderBy(orderList);

        final TypedQuery<T> typedQuery = entityManager.createQuery(query);
        if (entityGraph != null) {
            typedQuery.setHint(HIBERNATE_HINT_ENTITY_GRAPH,
                    entityManager.getEntityGraph(entityGraph));
        }

        final List<T> results = typedQuery.getResultList();

        return new PageImpl<>(
                results,
                pageQuery.toPageRequest(),
                page.getTotalElements()
        );
    }

    protected Page<ID> selectIdFindAll(Specification<T> specification, PageQuery pageQuery) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final CriteriaQuery<ID> query = criteriaBuilder.createQuery(this.entityIdType);
        final Root<T> root = query.from(this.entityType);

        query.select(root.get(this.entityIdField));
        query.where(specification.toPredicate(root, query, criteriaBuilder));

        // Remove if failing
        query.distinct(true);

        final TypedQuery<ID> typedQuery = this.entityManager.createQuery(query)
                .setMaxResults(pageQuery.getPageSize())
                .setFirstResult(pageQuery.getPageNumber() * pageQuery.getPageSize());

        final List<ID> resultList = typedQuery.getResultList();

        final Long count;
        if (pageQuery.getPageNumber() > 0 || resultList.size() == pageQuery.getPageSize()) {
            count = this.countDistinctBySpecification(specification);
        } else {
            count = (long) resultList.size();
        }

        return new PageImpl<>(resultList, pageQuery.toPageRequest(), count);
    }

    protected Long countDistinctBySpecification(Specification<T> specification) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        final Root<T> root = query.from(this.entityType);

        query.select(criteriaBuilder.countDistinct(root));

        final Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
        query.where(predicate);

        return this.entityManager.createQuery(query).getSingleResult();
    }

    protected <F> List<F> selectXFindAllDistinct(
            Specification<T> specification,
            SingularAttribute<T, F> field
    ) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final CriteriaQuery<F> query = criteriaBuilder.createQuery(field.getJavaType());
        final Root<T> root = query.from(this.entityType);

        query.select(root.get(field));
        query.where(specification.toPredicate(root, query, criteriaBuilder));

        query.distinct(true);

        final TypedQuery<F> typedQuery = this.entityManager.createQuery(query);

        return typedQuery.getResultList();
    }

    @Override
    @Transactional
    public List<T> findAll(Specification<T> specification, @Nullable String entityGraph) {
        final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final CriteriaQuery<T> query = criteriaBuilder.createQuery(this.entityType);
        final Root<T> root = query.from(this.entityType);

        query.where(specification.toPredicate(root, query, criteriaBuilder));

        final TypedQuery<T> typedQuery = this.entityManager.createQuery(query);
        if (entityGraph != null) {
            typedQuery.setHint(
                    HIBERNATE_HINT_ENTITY_GRAPH,
                    this.entityManager.getEntityGraph(entityGraph)
            );
        }

        return typedQuery.getResultList();
    }
}
