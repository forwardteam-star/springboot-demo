package com.darcytech.demo.spring.beanLifeCycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/*
 * 自定义处理器
 */
@Component
@Slf4j
public class TestInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
    /**
     * InstantiationAwareBeanPostProcessor中自定义的方法
     * 在方法实例化之前执行  Bean对象还没有
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if("user".equals(beanName)){
           log.info("【--InstantiationAwareBeanPostProcessor----】postProcessBeforeInstantiation");
        }
        return null;
    }

    /**
     * InstantiationAwareBeanPostProcessor中自定义的方法
     * 在方法实例化之后执行  Bean对象已经创建出来了
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if("user".equals(beanName)){
           log.info("【--InstantiationAwareBeanPostProcessor----】postProcessAfterInstantiation");
        }
        return true;
    }



    /*
     * InstantiationAwareBeanPostProcessor中自定义的方法
     * 可以用来修改Bean中属性的内容
     */
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if("user".equals(beanName)){
            log.info("【--InstantiationAwareBeanPostProcessor----】postProcessPropertyValues--->");
        }
        return pvs;
    }
}
