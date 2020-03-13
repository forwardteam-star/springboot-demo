package com.darcytech.demo.mysql.idgen;

import org.springframework.util.StringUtils;

import com.darcytech.demo.mysql.common.IdGenerator;
import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableIdGenerator implements IdGenerator {

    private final String sequenceName;

    private final int allocationSize;

    private final IdGenDao idGenDao;

    private long value;

    private long upperLimitValue;

    public TableIdGenerator(String sequenceName, int allocationSize, IdGenDao idGenDao) {
        Preconditions.checkArgument(StringUtils.hasText(sequenceName),"sequenceName不能为空");
        Preconditions.checkArgument(allocationSize> 0, "allocationSize 必须 > 0");

        this.sequenceName = sequenceName.replaceAll("\\s", "");

        Preconditions.checkArgument(this.sequenceName.length() == sequenceName.length(),
                "sequenceName不能包含空白字符（空格、Tab等）");

        this.allocationSize = allocationSize;
        this.idGenDao = idGenDao;
    }

    public synchronized long genNextId() {
        if (value >= upperLimitValue) {
            value = idGenDao.allocate(sequenceName, allocationSize);
            upperLimitValue = value + allocationSize;
        }
        return ++value;
    }

}
