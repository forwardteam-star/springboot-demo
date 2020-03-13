package com.darcytech.demo.web.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  <p>该注解不能和@LongToString 一起使用，否则无效
 *  <p>具体实现参考：com.darcytech.hermes.web.commons.CustomAnnotationIntrospector
 *  @see LongToString
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decrypt {

    boolean phone() default false;

}
