package com.darcytech.demo.mysql.common;

import javax.annotation.Nullable;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;

public interface IdGeneratorFactory {

    @Nullable
    IdGenerator createIfAnnotated(JpaEntityInformation<?, ?> info);

}
