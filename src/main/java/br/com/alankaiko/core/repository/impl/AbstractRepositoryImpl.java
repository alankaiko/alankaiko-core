package br.com.alankaiko.core.repository.impl;

import br.com.alankaiko.core.model.AbstractDTO;
import br.com.alankaiko.core.model.AbstractEntity;
import br.com.alankaiko.core.repository.AbstractRepository;
import br.com.alankaiko.core.utils.DynamicQuery;
import br.com.alankaiko.core.utils.ObjectUtil;
import br.com.alankaiko.core.utils.StringUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class AbstractRepositoryImpl<T extends AbstractEntity, D extends AbstractDTO, ID> extends SimpleJpaRepository<T, ID> implements AbstractRepository<T, D, ID> {
    private final EntityManager entityManager;
    protected Class<T> domainClass;
    protected Class<?> returnType;

    public AbstractRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        this(domainClass, domainClass, entityManager);
    }

    public AbstractRepositoryImpl(Class<T> domainClass, Class<?> returnType, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.domainClass = domainClass;
        this.returnType = returnType;
        this.entityManager = entityManager;
    }

    @Override
    public Page findAll(Specification spec, Pageable pageable) {
        return this.findAll(spec, pageable, this.returnType);
    }

    @Override
    public <U> Page<U> findAll(Specification<U> spec, Pageable pageable, Class<U> returnType) {
        TypedQuery<U> query = this.getQuery(spec, pageable, returnType);
        return (pageable.isUnpaged() ? new PageImpl(query.getResultList()) : this.getPage(query, pageable, spec));
    }

    @Override
    public Page<T> filtering(D filter) {
        return this.findAll(this.getFilterSpecification(filter), filter);
    }

    private <U> TypedQuery<U> getQuery(Specification<U> spec, Pageable pageable, Class<U> returnType) {
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return this.getQuery(spec, sort, returnType);
    }

    private <U> TypedQuery<U> getQuery(Specification<U> spec, Sort sort, Class<U> returnType) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<U> query = builder.createQuery(returnType);
        this.applySpecification(spec, query, builder, sort);

        return this.entityManager.createQuery(query);
    }

    private <U> Page<U> getPage(TypedQuery<U> query, Pageable pageable, Specification<U> spec) {
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable, () -> {
            return this.executeCountQuery(this.getCountTypedQuery(spec));
        });
    }

    private <U> void applySpecification(Specification<U> spec, CriteriaQuery<U> query, CriteriaBuilder builder, Sort sort) {
        Root root = query.from(super.getDomainClass());
        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, builder);
            if (!ObjectUtil.isNull(predicate)) {
                query.where(predicate);
            }
        }

        this.applySelection(root, query, builder);
        if (sort.isSorted())
            query.orderBy(QueryUtils.toOrders(sort, root, builder));

    }

    private long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query, "TypedQuery must not be null!");
        List<Long> totals = query.getResultList();
        long total = 0L;

        Long element;
        for (Iterator var5 = totals.iterator(); var5.hasNext(); total += element == null ? 0L : element) {
            element = (Long) var5.next();
        }

        return total;
    }

    private <U> void applySelection(Root root, CriteriaQuery<U> query, CriteriaBuilder builder) {
        List<Path> selectionList = new ArrayList();
        if (!query.getResultType().isAnnotationPresent(Entity.class)) {
            Map<String, From<?, ?>> joinMap = new HashMap();
            selectionList = this.getSelectionList(root, query, builder, joinMap);
        }

        if (!((List) selectionList).isEmpty()) {
            query.select(builder.construct(query.getResultType(), (Selection[]) ((List) selectionList).toArray((x$0) -> {
                return new Selection[x$0];
            })));
        } else if (ObjectUtil.isNull(query.getSelection())) {
            query.select(root);
        }
    }

    private <U> List<Path> getSelectionList(Root root, CriteriaQuery<U> query, CriteriaBuilder builder, Map<String, From<?, ?>> joinMap) {
        return (List) Arrays.stream(query.getResultType().getDeclaredFields()).map((field) -> {
            field.setAccessible(true);
            if (field.isAnnotationPresent(DynamicQuery.class)) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalArgumentException(String.format("Field %s must be final", field.getName()));
                } else {
                    DynamicQuery annotation = (DynamicQuery) field.getAnnotation(DynamicQuery.class);
                    String[] fields = annotation.field();
                    return fields.length > 1 ? this.getJoin(fields, root, query, builder, joinMap, 0, annotation, "").get(fields[fields.length - 1]) : root.get(fields[0]);
                }
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private <U> TypedQuery<Long> getCountTypedQuery(Specification<U> spec) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root root = query.from(this.getDomainClass());

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, builder);
            if (predicate != null)
                query.where(predicate);
        }

        if (query.isDistinct())
            query.select(builder.countDistinct(root));
        else
            query.select(builder.count(root));

        query.orderBy(Collections.emptyList());
        return this.entityManager.createQuery(query);
    }

    private From<?, ?> getJoin(String[] fields, From<?, ?> from, CriteriaQuery<?> query, CriteriaBuilder builder, Map<String, From<?, ?>> joinMap, int index, DynamicQuery annotation, String previous) {
        if (index == fields.length - 1) {
            return (From) joinMap.get(this.getKey(previous, fields[index - 1]));
        } else {
            String field = fields[index];
            Object join;

            if (joinMap.containsKey(this.getKey(previous, field))) {
                join = (From) joinMap.get(this.getKey(previous, field));
            } else {
                if (DynamicQuery.JoinType.COLUMN.equals(annotation.joinType()) && index == 0) {
                    join = this.getJoinByColumn(from, query, builder, field, annotation);
                } else {
                    join = from.join(field, this.getJoinType(annotation));
                }

                joinMap.put(this.getKey(previous, field), (From<?, ?>) join);
            }

            ++index;
            return this.getJoin(fields, (From) join, query, builder, joinMap, index, annotation, this.getPrevious(fields, previous, field));
        }
    }

    private String getPrevious(String[] fields, String previous, String field) {
        return StringUtil.isEmpty(previous) && fields.length > 2 ? field : previous;
    }

    private String getKey(String previous, String field) {
        return StringUtil.isEmpty(previous) ? field : String.join("-", previous, field);
    }

    private From<?, ?> getJoinByColumn(From<?, ?> from, CriteriaQuery<?> query, CriteriaBuilder builder, String field, DynamicQuery annotation) {
        Root<?> root = query.from(annotation.target());
        query.where(new Predicate[]{query.getRestriction(), builder.equal(from.get(field), root)});

        return root;
    }

    private JoinType getJoinType(DynamicQuery annotation) {
        try {
            return JoinType.valueOf(annotation.joinType().name());
        } catch (IllegalArgumentException var3) {
            return JoinType.LEFT;
        }
    }

    private Specification<T> getFilterSpecification(D filter) {
        try {
            String className = this.cutDTO(filter.getClass().getSimpleName()) + "Specifications";
            Class<?> clazz = Class.forName("br.com.docto.repository.specifications" + "." + className);
            Method method = clazz.getMethod("filteringByDto", filter.getClass());

            return (Specification<T>) method.invoke(null, filter);
        } catch (Exception e) {
            throw new RuntimeException("Este Objeto ainda não possui uma Classe Specification implementada " + filter.getClass());
        }
    }

    private String cutDTO(String value) {
        return value.substring(0, value.length() - 3);
    }

}
