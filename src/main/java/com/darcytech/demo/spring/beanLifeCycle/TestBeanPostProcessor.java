package com.darcytech.demo.spring.beanLifeCycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
/*
 * 自定义BeanPostProcessor实现类
 * BeanPostProcessor接口的作用是,我们可以通过该接口中的方法在bean实例化、配置以及其他初始化方法前后添加一些我们自己的逻辑
 */
public class TestBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            log.info("后置处理器 before方法: bean {} beanName:{}", bean, beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            log.info("后置处理器 after 方法 bean{} beanName:{}", bean, beanName);
        }
        return bean;
    }
}

