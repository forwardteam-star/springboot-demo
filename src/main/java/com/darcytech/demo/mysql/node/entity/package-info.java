@NonNullApi
@NonNullFields
@TypeDefs({
        @TypeDef(name = "Json", typeClass = JsonUserType.class),
        @TypeDef(name = "IntEnum", typeClass = IntEnumUserType.class)
})
package com.darcytech.demo.mysql.node.entity;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;

import com.darcytech.demo.mysql.common.IntEnumUserType;
import com.darcytech.demo.mysql.common.JsonUserType;