package com.darcytech.demo.mysql.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

public class IntToEnumConverter implements GenericConverter {

    private ConcurrentHashMap<Class, Map<Object, Object>> classIntEnumMap = new ConcurrentHashMap<>();

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> set = new HashSet<>();
        set.add(new ConvertiblePair(Integer.class, IntEnum.class));
        return set;
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class<?> targetClass = targetType.getObjectType();
        Map<Object, Object> map = classIntEnumMap.computeIfAbsent(targetClass, this::buildIntEnumMap);
        Object result = map.get(source);
        if (result == null) {
            throw new IllegalArgumentException("No enum instance for class: " + targetClass + ", value: " + source);
        }
        return result;
    }

    private Map<Object, Object> buildIntEnumMap(Class<?> clazz) {
        Map<Object, Object> map = new HashMap<>();
        for (Object v : clazz.getEnumConstants()) {
            int intValue = ((IntEnum) v).intValue();
            map.put(intValue, v);
        }
        return map;
    }
}

