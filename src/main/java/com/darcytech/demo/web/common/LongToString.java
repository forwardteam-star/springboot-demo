package com.darcytech.demo.web.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  <p>因为tid 和oid 传到前端会导致JS number 类型缺失精度，所以要将tid 和oid 转成字符串传给前端
 *  <p>该注解不能和 @Decrypt 注解一起使用,否则会导致@Decrypt 注解失效
 *  <p>按照业务需求没有同时要加@LongToString 和 @Decrypt 的场景，所以不用考虑同时使用的情况
 *  <p>具体实现参考：com.darcytech.hermes.web.commons.CustomAnnotationIntrospector
 *  @see Decrypt
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LongToString {

}
