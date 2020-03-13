package com.darcytech.demo.mysql.idgen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;

import static com.darcytech.demo.mysql.idgen.IdGenConfiguration.ID_GEN;

@Component
@Slf4j
class IdGenDao {

    @Qualifier(ID_GEN)
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 加上事务，再加上乐观锁。
     * 这样，即便在事务没有开启的情况下，也不会出现id重复的情况
     */
    @Transactional(transactionManager = IdGenConfiguration.TX_MANAGER_NAME)
    public long allocate(String sequenceName, int allocationSize) {
        String querySql = "select seq_value from global_id_seq where seq_name=? for update";
        Long lastValue = jdbcTemplate.queryForObject(querySql, Long.class, sequenceName);
        Preconditions.checkState(lastValue != null);

        String updateSql = "update global_id_seq set seq_value = ? where seq_name=? and seq_value=?";
        int rows = jdbcTemplate.update(updateSql, lastValue + allocationSize, sequenceName, lastValue);
        Preconditions.checkArgument(rows == 1, "必须能更新到一条记录");

        return lastValue;
    }

}
