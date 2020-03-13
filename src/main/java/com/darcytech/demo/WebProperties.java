package com.darcytech.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@ConfigurationProperties(prefix = "web")
@Configuration
@Data
public class WebProperties {

    private String name;

    private boolean stop = false;

    private int mainThreadPoolSize = 5;
}
