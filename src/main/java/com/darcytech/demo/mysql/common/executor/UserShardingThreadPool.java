package com.darcytech.demo.mysql.common.executor;

import java.util.List;
import java.util.concurrent.Callable;

import com.darcytech.demo.mysql.common.DataSourceRouter;
import com.google.common.base.Preconditions;

public class UserShardingThreadPool extends ShardingThreadPool {

    private final ServerUserMapper serverUserMapper;

    public UserShardingThreadPool(DataSourceRouter dataSourceRouter, ServerUserMapper serverUserMapper) {
        super(dataSourceRouter);
        this.serverUserMapper = serverUserMapper;
    }

    public void userExecute(Long userId, Runnable task) {
        dbExecute(checkAndGetServerId(userId), task);
    }

    public void userTryExecute(Long userId, String taskKey, Runnable task) {
        dbTryExecute(checkAndGetServerId(userId), taskKey, task);
    }

    public void userRunAll(Long userId, Iterable<Runnable> tasks) {
        dbRunAll(checkAndGetServerId(userId), tasks);
    }

    public <T> List<T> userCallAll(Long userId, Iterable<Callable<T>> tasks) {
        return dbCallAll(checkAndGetServerId(userId), tasks);
    }

    public <T> List<T> userFlatCallAll(Long userId, Iterable<Callable<Iterable<T>>> tasks) {
        return dbFlatCallAll(checkAndGetServerId(userId), tasks);
    }

    private Integer checkAndGetServerId(Long userId) {
        Integer serverId = serverUserMapper.resolveServerId(userId);
        Preconditions.checkState(serverId != null, "serverId is null for user: %s", userId);
        return serverId;
    }

}
