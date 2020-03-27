package com.darcytech.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.RetryConfiguration;

import com.alibaba.nacos.client.config.utils.SnapShotSwitch;
import com.darcytech.demo.nacos.NacosConfigApplicationInitializer;
import com.darcytech.demo.spring.beanLifeCycle.User;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableRetry
public class WebMain {

    public WebMain(RetryConfiguration retryConfig) {
        retryConfig.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    public static void main(String[] args) {
        SnapShotSwitch.setIsSnapShot(false);
        SpringApplication springApplication = new SpringApplication(WebMain.class);
        springApplication.addInitializers(new NacosConfigApplicationInitializer(null));
        springApplication.run(args);
    }

    @Bean
    @ConfigurationProperties("demo")
    public Demo demo () {
        return new Demo();
    }

    @Bean(initMethod = "start",destroyMethod = "stop")
    public User user(){
        return new User();
    }

}
