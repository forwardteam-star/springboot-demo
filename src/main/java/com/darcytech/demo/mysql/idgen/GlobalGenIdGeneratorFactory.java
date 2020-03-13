package com.darcytech.demo.mysql.idgen;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Component;

import com.darcytech.demo.mysql.common.GlobalGen;
import com.darcytech.demo.mysql.common.IdGenerator;
import com.darcytech.demo.mysql.common.IdGeneratorFactory;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GlobalGenIdGeneratorFactory implements IdGeneratorFactory {

    @Autowired
    private IdGenDao idGenDao;

    @SneakyThrows
    @Nullable
    public IdGenerator createIfAnnotated(JpaEntityInformation<?, ?> info) {
        Iterable<String> idAttributeNames = info.getIdAttributeNames();
        Class<?> entityClass = info.getJavaType();
        GlobalGen globalGen = null;
        for (String attrName : info.getIdAttributeNames()) {
            Field field = entityClass.getDeclaredField(attrName);
            globalGen = field.getAnnotation(GlobalGen.class);
            if (globalGen != null) {
                break;
            }
        }

        if (globalGen == null) {
            return null;
        }

        if (!Number.class.isAssignableFrom(info.getIdType())) {
            log.warn("global id generator cannot be used for non number id. entityClass: {}, idType: {}",
                    entityClass.getCanonicalName(), info.getIdType());
            return null;
        }

        String sequenceName = globalGen.value();
        return new TableIdGenerator(sequenceName, globalGen.allocationSize(), idGenDao);
    }

}
