package com.darcytech.demo.mysql.catalog.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.darcytech.demo.mysql.catalog.CatalogTxReadOnly;
import com.darcytech.demo.mysql.catalog.entity.Server;
import com.darcytech.demo.mysql.common.DxJdbcRowMapper;

/**
 * 这里不使用 Spring Data Jpa， 是因为 Spring Data Jpa 在初始化 jpaMappingContext 的时候会从 applicationContext 中获取所有
 * EntityManagerFactory，而 node 中 EntityManagerFactory 又反过来依赖了 ServerDao，从而造成了循环依赖。
 *
 * @see org.springframework.data.jpa.repository.config.JpaMetamodelMappingContextFactoryBean#getMetamodels()
 */
@SuppressWarnings("JavadocReference")
@CatalogTxReadOnly
@Component
public class ServerDao {

    @Autowired
    private DataSource catalogDataSource;

    public List<Server> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(catalogDataSource);
        String sql = "select id, jdbc_url, username, password from server";
        return jdbcTemplate.query(sql, new DxJdbcRowMapper<>(Server.class));
    }

}
