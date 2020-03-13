package com.darcytech.demo.mysql.common;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.util.Assert;

public class EnhancedJpaRepositoryFactory extends JpaRepositoryFactory {

    private final IdGeneratorFactory generatorFactory;

    public EnhancedJpaRepositoryFactory(EntityManager entityManager, IdGeneratorFactory generatorFactory) {
        super(entityManager);
        this.generatorFactory = generatorFactory;
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation info,
                                                                    EntityManager em) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(info.getDomainType());
        Object repository = getTargetRepositoryViaReflection(info, entityInformation, em, generatorFactory);

        Assert.isInstanceOf(JpaRepositoryImplementation.class, repository);

        return (JpaRepositoryImplementation<?, ?>) repository;
    }

}
