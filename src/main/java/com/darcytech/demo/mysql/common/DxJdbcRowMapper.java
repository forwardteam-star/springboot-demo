package com.darcytech.demo.mysql.common;

import java.util.Map;

import org.hibernate.annotations.Type;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ConcurrentReferenceHashMap;

public class DxJdbcRowMapper<T> extends org.springframework.jdbc.core.BeanPropertyRowMapper<T> {

    private static final ConversionService conversionService = createForHibernateCustomType();

    public DxJdbcRowMapper() {
        setConversionService(conversionService);
    }

    public DxJdbcRowMapper(Class<T> mappedClass) {
        this(mappedClass, false);
    }

    public DxJdbcRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
        super(mappedClass, checkFullyPopulated);
        setConversionService(conversionService);
    }

    private static DefaultConversionService createForHibernateCustomType() {
        JsonToObjectConverter noMatch = new JsonToObjectConverter();
        JsonToObjectConverter jsonConverter = new JsonToObjectConverter();
        Map<TypeDescriptor, GenericConverter> cache = new ConcurrentReferenceHashMap<>(1024);

        DefaultConversionService conversionService = new DefaultConversionService() {
            @Override
            protected GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
                GenericConverter converter = cache.computeIfAbsent(targetType, key -> annotatedByJson(key) ? jsonConverter : noMatch);
                return converter != noMatch ? converter : super.getConverter(sourceType, targetType);
            }
        };
        conversionService.addConverter(new IntToEnumConverter());
        return conversionService;
    }

    private static boolean annotatedByJson(TypeDescriptor targetType) {
        Type typeAnnotation = targetType.getAnnotation(Type.class);
        return typeAnnotation != null && "Json".equals(typeAnnotation.type());
    }

}
