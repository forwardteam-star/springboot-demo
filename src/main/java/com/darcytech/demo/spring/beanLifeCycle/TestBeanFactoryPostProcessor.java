package com.darcytech.demo.spring.beanLifeCycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
/*
 * 自定义的BeanFactoryPostProcessor，测试生命周期
 */
public class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    /**
     * 本方法在Bean对象实例化之前执行，
     * 通过beanFactory可以获取bean的定义信息，
     * 并可以修改bean的定义信息。这点是和BeanPostProcessor最大区别
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            if ("user".contains(name)) {
                log.info("***BeanFactoryPostProcessor 开始执行了****");
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
                MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
                if (propertyValues.contains("name")) {
                    propertyValues.addPropertyValue("name", "start");
                    log.info("修改了属性信息");
                }
                log.info("*******BeanFactoryPostProcessor 执行结束了****");
            }
        }
    }
}
