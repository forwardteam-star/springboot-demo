package com.darcytech.demo.mysql.common;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface GlobalGen {

    @AliasFor(attribute = "sequenceName")
    String value() default "";

    @AliasFor(attribute = "value")
    String sequenceName() default "";

    int allocationSize() default 10000;

}
