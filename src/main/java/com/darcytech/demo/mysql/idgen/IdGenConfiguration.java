package com.darcytech.demo.mysql.idgen;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import static com.darcytech.demo.common.PropertiesBinder.bind;

@Configuration
public class IdGenConfiguration {

    public static final String ID_GEN = "id_gen";

    public static final String TX_MANAGER_NAME = "idGenTransactionManager";

    @Autowired
    private DataSourceProperties dataSourceProps;

    @Autowired
    private ApplicationContext context;

    @Bean
    @Qualifier(ID_GEN)
    public DataSource idGenDataSource() {
        HikariDataSource result = dataSourceProps.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        return bind(context, result, "spring.datasource.hikari", "idgen.datasource.hikari");
    }

    @Bean
    @Qualifier(ID_GEN)
    public DataSourceTransactionManager idGenTransactionManager() {
        return new DataSourceTransactionManager(idGenDataSource());
    }

    @Bean
    @Qualifier(ID_GEN)
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(idGenDataSource());
    }

}
