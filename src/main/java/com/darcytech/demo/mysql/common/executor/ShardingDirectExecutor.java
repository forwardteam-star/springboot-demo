package com.darcytech.demo.mysql.common.executor;

import java.util.concurrent.Callable;

import com.darcytech.demo.mysql.common.ThreadLocalDataSourceSelector;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class ShardingDirectExecutor {

    private final ThreadLocalDataSourceSelector dataSourceSelector;

    public void dbDirectExecute(Integer serverId, Runnable task) {
        dbDirectExecute(serverId, () -> {
            task.run();
            return null;
        });
    }

    @SneakyThrows
    public <T> T dbDirectExecute(Integer serverId, Callable<T> task) {
        Integer originalKey = dataSourceSelector.get();
        try {
            dataSourceSelector.set(serverId);
            return task.call();
        } finally {
            if (originalKey != null) {
                dataSourceSelector.set(originalKey);
            } else {
                dataSourceSelector.clear();
            }
        }
    }

}
