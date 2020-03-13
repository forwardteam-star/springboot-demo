package com.darcytech.demo.mysql.common;

import java.util.List;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;

public class EnhancedJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends JpaRepositoryFactoryBean<T, S, ID> {

    private EntityPathResolver entityPathResolver;

    private IdGeneratorFactory generatorFactory;

    @Nullable
    @Autowired(required = false)
    private List<RepositoryProxyPostProcessor> repositoryProxyPostProcessors;

    public EnhancedJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    @Autowired
    public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
        super.setEntityPathResolver(resolver);
        this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
    }

    @Autowired
    public void setGeneratorFactory(IdGeneratorFactory generatorFactory) {
        this.generatorFactory = generatorFactory;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        EnhancedJpaRepositoryFactory jpaRepositoryFactory = new EnhancedJpaRepositoryFactory(entityManager, generatorFactory);
        jpaRepositoryFactory.setEntityPathResolver(entityPathResolver);
        if(repositoryProxyPostProcessors != null) {
            repositoryProxyPostProcessors.forEach(jpaRepositoryFactory::addRepositoryProxyPostProcessor);
        }

        return jpaRepositoryFactory;
    }

}
