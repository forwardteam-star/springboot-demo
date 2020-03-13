package com.darcytech.demo.mysql.common;

import java.util.HashMap;
import java.util.Map;

public interface JdbcServer {

    Integer getId();

    String getJdbcUrl();

    String getUsername();

    String getPassword();

    default Map<String, String> asMap() {
        Map<String, String> map = new HashMap<>();
        map.put("jdbcUrl", getJdbcUrl());
        map.put("username", getUsername());
        map.put("password", getPassword());
        return map;
    }

}
