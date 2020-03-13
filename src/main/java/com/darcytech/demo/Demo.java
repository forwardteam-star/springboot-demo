package com.darcytech.demo;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Demo implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ThreadPoolExecutor mainThreadExecutor;

    @Autowired
    private WebProperties webProperties;

    @PostConstruct
    public void init() {
        mainThreadExecutor.setCorePoolSize(webProperties.getMainThreadPoolSize());
        mainThreadExecutor.setMaximumPoolSize(webProperties.getMainThreadPoolSize());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    }

}

