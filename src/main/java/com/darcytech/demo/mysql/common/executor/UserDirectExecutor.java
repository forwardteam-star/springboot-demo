package com.darcytech.demo.mysql.common.executor;

import java.util.concurrent.Callable;

import com.darcytech.demo.mysql.common.ThreadLocalDataSourceSelector;
import com.google.common.base.Preconditions;

import lombok.SneakyThrows;

public class UserDirectExecutor extends ShardingDirectExecutor {

    private final ServerUserMapper serverUserMapper;

    public UserDirectExecutor(ServerUserMapper serverUserMapper, ThreadLocalDataSourceSelector dataSourceSelector) {
        super(dataSourceSelector);
        this.serverUserMapper = serverUserMapper;
    }

    public void userDirectExecute(Long userId, Runnable task) {
        userDirectExecute(userId, () -> {
            task.run();
            return null;
        });
    }

    @SneakyThrows
    public <T> T userDirectExecute(Long userId, Callable<T> task) {
        return dbDirectExecute(checkAndGetServerId(userId), task);
    }

    private Integer checkAndGetServerId(Long userId) {
        Integer serverId = serverUserMapper.resolveServerId(userId);
        Preconditions.checkState(serverId != null, "serverId is null for user: %s", userId);
        return serverId;
    }

}
