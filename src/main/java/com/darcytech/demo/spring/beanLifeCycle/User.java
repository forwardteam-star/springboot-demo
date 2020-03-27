package com.darcytech.demo.spring.beanLifeCycle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/*
 *测试实体类
 * 描述一下比较完整的生命周期
 * 1.首先初始化一个bean是，首先是执行BeanFactoryPostProcessor中的接口方法 postProcessBeanFactory()，这个时候bean还未初始化
 * 2.如果InstantiationAwareBeanPostProcessor的实现类，则会先走该类中的postProcessBeforeInstantiation(实例化执行走的方法)
 * 3.调用bean的构造方法进行实例化，然后就是bean的属性注入
 * 4.接着调用beanNameAware,FactoryBeanAware,ApplicationContextAware,EnvironmentAware这些让spring容器感知
 * 5.调用BeanPostProcessor的前置方法postProcessorBeforeInitilzaiton
 * 6.bean有实现注解@postConstrut,则执行该方法
 * 7.实现InitializingBean执行afterPropertiesSet
 * 8.自定义的init方法
 * 9.最后调用beanPostProcessor中的postProcessorAfterInitialization(对bean进行最后的处理)
 * 如果容器关闭，
 * 10.@preDestory的关闭方法，
 * 11.DisposableBean的destory方法
 * 12.自定义的destory方法，可以做一些类似的优雅关闭
 */
@Getter
@Setter
@Slf4j
@ToString
public class User implements InitializingBean, DisposableBean, BeanNameAware,ApplicationContextAware, BeanFactoryAware, EnvironmentAware {

    private int id;

    private String name;

    //感知本对象在Spring容器中的id属性
    private String beanName;
    // 感知本对象所属的BeanFactory对象
    private BeanFactory factory;

    public User(){
        log.info("构造方法被执行了...User被实例化");
    }

    public void setName(String name){
        log.info("《注入属性》注入name属性");
        this.name = name;
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info("【BeanFactoryAware接口】setBeanFactory");
        this.factory = beanFactory;
    }

    @Override
    public void setBeanName(String name) {
        log.info("【BeanNameWare接口】setBeanName");
        this.beanName = name;
    }

    @Override
    public void destroy() {
        log.info("《DisposableBean接口》destory ....");
    }

    @Override
    public void afterPropertiesSet() {
        log.info("初始化:《InitializingBean接口》afterPropertiesSet....");
    }

    @PostConstruct
    public void postConstruct(){
        log.info("初始化:【@PostConstruct】执行了...");
    }

    /**
     * 销毁前的回调方法
     */
    @PreDestroy
    public void preDestory(){
        log.info("【@preDestory】执行了...");
    }

    public void start(){
       log.info("初始化:【init-method】方法执行了....");
    }

    /**
     * 销毁前的回调方法
     * 通过bean标签中的 destory-method属性指定
     */
    public void stop(){
        log.info("【destory-method】方法执行了....");
    }

    @Override
    public void setEnvironment(Environment environment) {
        log.info("environment:{}",environment.getProperty("spring.application.name"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("applicationContext:{}",applicationContext);
    }
}
