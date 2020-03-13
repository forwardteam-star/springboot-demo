package com.darcytech.demo.mysql.node;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.domain.support.AuditingBeanFactoryPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.darcytech.demo.mysql.catalog.dao.ServerDao;
import com.darcytech.demo.mysql.common.DataSourceRouter;
import com.darcytech.demo.mysql.common.DataSourceSelector;
import com.darcytech.demo.mysql.common.EnhancedJpaRepository;
import com.darcytech.demo.mysql.common.EnhancedJpaRepositoryFactoryBean;
import com.darcytech.demo.mysql.common.ThreadLocalDataSourceSelector;
import com.darcytech.demo.mysql.node.entity.User;
import com.google.common.collect.ImmutableMap;

import static com.darcytech.demo.mysql.node.NodeConfiguration.TX_MANAGER_NAME;

@Configuration
@EnableJpaRepositories(
        transactionManagerRef = TX_MANAGER_NAME,
        entityManagerFactoryRef = "nodeEntityManagerFactory",
        repositoryBaseClass = EnhancedJpaRepository.class,
        repositoryFactoryBeanClass = EnhancedJpaRepositoryFactoryBean.class
)
@EnableTransactionManagement
public class NodeConfiguration {

    public static final String TX_MANAGER_NAME = "nodeTransactionManager";

    public static final String UNIT_NAME = "node";

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Bean
    @Qualifier(UNIT_NAME)
    public DataSource nodeDataSource() {
        return new DataSourceRouter(serverDao.findAll(), "node.datasource.hikari");
    }

    /**
     * 允许其他模块覆盖此定义
     */
    @ConditionalOnMissingBean(DataSourceSelector.class)
    @Bean
    public ThreadLocalDataSourceSelector threadLocalServerSelector() {
        return new ThreadLocalDataSourceSelector();
    }

    @Bean
    @Qualifier(UNIT_NAME)
    public LocalContainerEntityManagerFactoryBean nodeEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(nodeDataSource())
                .packages(User.class, Jsr310JpaConverters.class)
                .persistenceUnit(UNIT_NAME)
                .mappingResources("jpa-audit.xml")
                .properties(ImmutableMap.of("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory)))
                .build();
    }

    @Bean
    @Qualifier(UNIT_NAME)
    public PlatformTransactionManager nodeTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setPersistenceUnitName(UNIT_NAME);
        return transactionManager;
    }

    @ConditionalOnMissingBean(AuditingBeanFactoryPostProcessor.class)
    @EnableJpaAuditing
    public static class JpaAuditingConfiguration {
    }

}
