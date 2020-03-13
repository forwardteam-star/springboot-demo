package com.darcytech.demo.mysql.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnhancedJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseJpaRepository<T, ID> {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final JpaEntityInformation<T, ID> entityInformation;

    private final EntityManager entityManager;

    @Nullable
    private final IdGenerator idGen;

    private final String idPropertyName;

    public EnhancedJpaRepository(JpaEntityInformation<T, ID> entityInformation,
                                 EntityManager entityManager, IdGeneratorFactory generatorFactory) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDataSource(entityManager));
        this.idGen = generatorFactory.createIfAnnotated(entityInformation);
        this.idPropertyName = entityInformation.getIdAttributeNames().iterator().next();
    }

    public JdbcTemplate getJdbcTemplate() {
        return (JdbcTemplate) namedParameterJdbcTemplate.getJdbcOperations();
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public <S extends T> S save(S entity) {
        if (idGen == null) {
            return super.save(entity);
        }
        if (entityInformation.isNew(entity)) {
            BeanWrapper wrapper = new DirectFieldAccessFallbackBeanWrapper(entity);
            wrapper.setPropertyValue(idPropertyName, idGen.genNextId());
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Transactional
    public void update(T entity) {
        Session session = entityManager.unwrap(Session.class);
        session.update(entity);
    }

    @Transactional
    public void update(Iterable<T> entities) {
        Session session = entityManager.unwrap(Session.class);
        for (T e : entities) {
            session.update(e);
        }
    }

    private DataSource getDataSource(EntityManager entityManager) {
        SessionFactoryImpl sf = entityManager.getEntityManagerFactory().unwrap(SessionFactoryImpl.class);
        return ((DatasourceConnectionProviderImpl) sf.getServiceRegistry().getService(ConnectionProvider.class)).getDataSource();
    }

    public List<T> findBySpecNoCount(Specification<T> spec, Pageable pageable) {
        TypedQuery<T> query = getQuery(spec, pageable);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }

    @Override
    public List<T> findAllByIdReadOnly(Collection<ID> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        boolean currentTransactionReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        // 此方法采用分多步查询的方式，以避免 hibernate 查询缓存内存溢出和缓存失效（QueryPlanCache）
        // 必须在只读事务中调用，是因为分步查询会引发 hibernate Dirty Check，从而导致查询性能下降，CPU 耗尽
        Preconditions.checkState(currentTransactionReadOnly, "必须在只读事务中调用此方法");
        List<T> result = new ArrayList<>(ids.size());
        for (List<ID> partIds : Iterables.partition(Sets.newLinkedHashSet(ids), 256)) {
            result.addAll(findAllById(partIds));
        }
        return result;
    }

}
