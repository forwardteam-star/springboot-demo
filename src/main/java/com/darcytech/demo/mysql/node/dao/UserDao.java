package com.darcytech.demo.mysql.node.dao;

import com.darcytech.demo.mysql.node.NodeTxReadOnly;
import com.darcytech.demo.mysql.node.entity.User;
import com.darcytech.demo.mysql.common.BaseJpaRepository;
@NodeTxReadOnly
public interface UserDao extends BaseJpaRepository<User, Long> {
}
