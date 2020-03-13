package com.darcytech.demo.mysql.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;
import org.springframework.lang.Nullable;

public class IntEnumUserType implements UserType, DynamicParameterizedType {

    private Class<?> returnedClass;

    private Map<Integer, Object> valueMap;

    @Override
    public void setParameterValues(Properties parameters) {

        Field field = HibernateUtils.resolveAnnotatedField(parameters);
        returnedClass = field.getType();

        Object[] values = returnedClass.getEnumConstants();
        if (values == null || values.length == 0 || !(values[0] instanceof IntEnum)) {
            throw new IllegalArgumentException(returnedClass + " should implement the IntEnum interface.");
        }
        Map<Integer, Object> map = new HashMap<>(values.length);
        for (Object v : values) {
            int intValue = ((IntEnum) v).intValue();
            if (map.containsKey(intValue)) {
                throw new IllegalArgumentException("Duplicate IntValue " + intValue);
            }
            map.put(intValue, v);
        }
        this.valueMap = map;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.INTEGER};
    }

    @Override
    public Class returnedClass() {
        return returnedClass;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(@Nullable Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Nullable
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session,
                              Object owner) throws HibernateException, SQLException {
        Integer value = rs.getObject(names[0], Integer.class);
        if (value == null) {
            return null;
        }
        Object result = valueMap.get(value);
        if (result == null) {
            throw new IllegalStateException("Cannot map int[" + value + "] to " + returnedClass);
        }
        return result;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, @Nullable Object value, int index,
                            SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.INTEGER);
            return;
        }
        if (!(value instanceof IntEnum)) {
            throw new AssertionError("value is not an instance of IntEnum");
        }
        st.setInt(index, ((IntEnum) value).intValue());
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
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