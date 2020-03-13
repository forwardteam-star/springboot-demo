package com.darcytech.demo.web.common;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class WebJacksonIntrospector extends JacksonAnnotationIntrospector {

    private static final long serialVersionUID = 252823090113586398L;

    @Override
    @Nullable
    public Object findSerializer(Annotated a) {
        JavaType type = a.getType();
        if (type.isContainerType()) {
            return null;
        }
        return findSerializerInternal(a, type);
    }

    @Override
    @Nullable
    public Object findContentSerializer(Annotated a) {
        JavaType type = a.getType();
        if (!type.isContainerType()) {
            return null;
        }
        return findSerializerInternal(a, type.getContentType());
    }

    private Object findSerializerInternal(Annotated a, JavaType target) {
        Decrypt decrypt = a.getAnnotation(Decrypt.class);
        if (decrypt != null) {
            if (target.hasRawClass(String.class)) {
                return new DecryptSerializer(decrypt.phone());
            } else {
                throw new IllegalArgumentException("Decrypt annotation cannot be used for types other than String.");
            }
        }

        LongToString longToString = a.getAnnotation(LongToString.class);
        if (longToString != null) {
            if (target.hasRawClass(Long.class)) {
                return new LongToStringSerializer();
            } else {
                throw new IllegalArgumentException("Decrypt annotation cannot be used for types other than Long.");
            }
        }
        return null;
    }

}
