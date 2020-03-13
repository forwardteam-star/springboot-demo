package com.darcytech.demo.mysql.common;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUserType implements UserType, DynamicParameterizedType {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Class<?> returnedClass;

    private JavaType targetJavaType;

    @Override
    public void setParameterValues(Properties parameters) {
        Field annotatedField = HibernateUtils.resolveAnnotatedField(parameters);
        returnedClass = annotatedField.getType();
        targetJavaType = objectMapper.getTypeFactory().constructType(annotatedField.getGenericType());
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.CLOB};
    }

    @Override
    public Class<?> returnedClass() {
        return returnedClass;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Nullable
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        return fromJson(value);
    }

    @Nullable
    private Object fromJson(@Nullable String value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(value, targetJavaType);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot parse json: " + value + ", to " + returnedClass);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, @Nullable Object value, int index,
                            SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.CLOB);
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(value);
            st.setString(index, json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot convert " + value + " to json");
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        throw new UnsupportedOperationException("deepCopy is not supported");
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        throw new UnsupportedOperationException("disassemble is not supported");
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        throw new UnsupportedOperationException("assemble is not supported");
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}

