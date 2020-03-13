package com.darcytech.demo.mysql.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.darcytech.demo.common.PropertiesBinder;
import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;

@Getter
public class DataSourceRouter extends AbstractRoutingDataSource {

    private final List<? extends JdbcServer> servers;

    private final String configPrefix;

    @SuppressWarnings("NullableProblems")
    @Autowired
    private ApplicationContext applicationContext;

    @SuppressWarnings("NullableProblems")
    @Autowired
    private ThreadLocalDataSourceSelector dataSourceSelector;

    public DataSourceRouter(List<? extends JdbcServer> servers, String configPrefix) {
        this.servers = servers;
        this.configPrefix = configPrefix;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dataSourceSelector.get();
    }

    @Override
    public void afterPropertiesSet() {
        Preconditions.checkArgument(servers.size() > 0, "必须指定至少一个server");
        setTargetDataSources(buildDataSources(servers));
        super.afterPropertiesSet();
    }

    private Map<Object, Object> buildDataSources(List<? extends JdbcServer> servers) {
        PropertiesBinder binder = new PropertiesBinder(applicationContext);
        Map<Object, Object> dataSources = new HashMap<>(servers.size());
        for (JdbcServer s : servers) {
            dataSources.put(s.getId(), build(binder, s));
        }
        return dataSources;
    }

    private DataSource build(PropertiesBinder binder, JdbcServer server) {
        HikariDataSource result = new HikariDataSource();

        result = binder.bind(result, "spring.datasource.hikari", configPrefix);

        result.setJdbcUrl(server.getJdbcUrl());
        result.setUsername(server.getUsername());
        result.setPassword(server.getPassword());

        return result;
    }

}
