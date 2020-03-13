package com.darcytech.demo.mysql.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public interface BaseJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    JdbcTemplate getJdbcTemplate();

    NamedParameterJdbcTemplate getNamedParameterJdbcTemplate();

    EntityManager getEntityManager();

    void update(T entity);

    void update(Iterable<T> entities);

    List<T> findBySpecNoCount(Specification<T> spec, Pageable pageable);

    List<T> findAllByIdReadOnly(Collection<ID> ids);

    static <T, Y extends Comparable<? super Y>> Specification<T> isNull(SingularAttribute<T, Y> attr) {
        return (root, query, cb) -> cb.isNull(root.get(attr));
    }

    static <T, Y extends Comparable<? super Y>> Specification<T> isNotNull(SingularAttribute<T, Y> attr) {
        return (root, query, cb) -> cb.isNotNull(root.get(attr));
    }

    @Nullable
    static <T> Specification<T> eq(SingularAttribute<T, ?> attr, @Nullable Object value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.equal(root.get(attr), value);
    }

    @Nullable
    static <T> Specification<T> startsWith(SingularAttribute<T, ?> attr, @Nullable Object value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.like(root.<String>get(attr.getName()), value.toString() + "%");
    }

    @Nullable
    static <T> Specification<T> in(SingularAttribute<T, ?> attr, Collection<?> values) {
        if (isNullOrEmpty(values)) {
            return null;
        }
        return (root, query, cb) -> {
            Path<?> expression = root.get(attr);
            ParameterExpression<Iterable> param = cb.parameter(Iterable.class);
            return cb.isTrue(expression.in(values));
        };
    }

    @Nullable
    static <T> Specification<T> notIn(SingularAttribute<T, ?> attr, Collection<?> values) {
        if (isNullOrEmpty(values)) {
            return null;
        }
        return (root, query, cb) -> {
            Path<?> expression = root.get(attr);
            return cb.isTrue(expression.in(values).not());
        };
    }

    @Nullable
    static <T, Y extends Comparable<? super Y>> Specification<T> lt(SingularAttribute<T, Y> attr, Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.lessThan(root.get(attr), value);
    }

    @Nullable
    static <T, Y extends Comparable<? super Y>> Specification<T> lte(SingularAttribute<T, Y> attr, @Nullable Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attr), value);
    }

    @Nullable
    static <T, Y extends Comparable<? super Y>> Specification<T> gt(SingularAttribute<T, Y> attr, Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.greaterThan(root.get(attr), value);
    }

    @Nullable
    static <T, Y extends Comparable<? super Y>> Specification<T> gte(SingularAttribute<T, Y> attr, @Nullable Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(attr), value);
    }

    @Nullable
    static <T> Specification<T> like(SingularAttribute<T, String> attr, String value) {
        if (isNullOrEmpty(value)) {
            return null;
        }
        //由于mysql查询_会转义，like默认不带%的模糊搜索
        value = value.replaceAll("_", "\\\\_");
        String finalValue = value;
        return (root, query, cb) -> cb.like(root.get(attr), "%" + finalValue + "%");
    }

    @Nullable
    static <T, Y extends Comparable<? super Y>> Specification<T> between(SingularAttribute<T, Y> attr, @Nullable Y from, @Nullable Y to) {
        if (from == null) {
            return lte(attr, to);
        }
        if (to == null) {
            return gte(attr, from);
        }
        return (root, query, cb) -> cb.between(root.get(attr), from, to);
    }

    @Nullable
    static <T, J> Specification<T> eq(SingularAttribute<T, J> joinAttr, SingularAttribute<J, ?> attr, @Nullable Object value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.equal(root.join(joinAttr, JoinType.LEFT).get(attr), value);
    }

    @Nullable
    static <T, J> Specification<T> lt(SingularAttribute<T, J> joinAttr,
                                      SingularAttribute<J, ? extends Number> attr, Number value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.lt(root.join(joinAttr, JoinType.LEFT).get(attr), value);
    }

    @Nullable
    static <T, Y extends Comparable<? super Y>> Specification<T> le(SingularAttribute<T, Y> attr, Y value) {
        return isNullOrEmpty(value) ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.get(attr), value);
    }

    static boolean isNullOrEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    static boolean isNullOrEmpty(@Nullable Object obj) {
        return obj == null || obj instanceof String && ((String) obj).isEmpty();
    }

}
