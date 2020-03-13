package com.darcytech.demo.mysql.catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingBeanFactoryPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.darcytech.demo.mysql.catalog.entity.Server;
import com.darcytech.demo.mysql.common.EnhancedJpaRepository;
import com.darcytech.demo.mysql.common.EnhancedJpaRepositoryFactoryBean;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "catalogEntityManagerFactory",
        transactionManagerRef = CatalogConfiguration.TX_MANAGER_NAME,
        repositoryBaseClass = EnhancedJpaRepository.class,
        repositoryFactoryBeanClass = EnhancedJpaRepositoryFactoryBean.class
)
@EnableTransactionManagement
public class CatalogConfiguration {

    public static final String UNIT_NAME = "catalog";

    public static final String TX_MANAGER_NAME = "catalogTransactionManager";

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Bean
    @Qualifier(UNIT_NAME)
    @Primary
    @ConfigurationProperties(value="spring.datasource.hikari")
    public HikariDataSource catalogDataSource() {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Qualifier(UNIT_NAME)
    @Primary
    public PlatformTransactionManager catalogTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setPersistenceUnitName(UNIT_NAME);
        return transactionManager;
    }

    @Bean
    @Qualifier(UNIT_NAME)
    @Primary
    public LocalContainerEntityManagerFactoryBean catalogEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(catalogDataSource())
                .packages(Server.class, Jsr310JpaConverters.class)
                .persistenceUnit(UNIT_NAME)
                .properties(ImmutableMap.of("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory)))
                .mappingResources("jpa-audit.xml")
                .build();
    }

    @ConditionalOnMissingBean(AuditingBeanFactoryPostProcessor.class)
    @EnableJpaAuditing
    public static class JpaAuditingConfiguration {
    }

}

