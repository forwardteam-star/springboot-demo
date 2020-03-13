package com.darcytech.demo.mysql.common;

import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.usertype.DynamicParameterizedType;

public class HibernateUtils {

    private HibernateUtils() {
    }

    public static Field resolveAnnotatedField(Properties parameters) {
        Object xProperty = parameters.get(DynamicParameterizedType.XPROPERTY);
        try {
            Field field = JavaXMember.class.getSuperclass().getDeclaredField("annotatedElement");
            field.setAccessible(true);
            return (Field) field.get(xProperty);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

}
