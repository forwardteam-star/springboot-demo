package com.darcytech.demo.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import com.darcytech.demo.WebProperties;

@Configuration
public class ExecutorConfiguration {

    @Autowired
    private WebProperties webProperties;

    @Bean(destroyMethod = "shutdownNow")
    public ThreadPoolExecutor mainThreadExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(webProperties.getMainThreadPoolSize(),
                new CustomizableThreadFactory("userThread-"));
    }
}
