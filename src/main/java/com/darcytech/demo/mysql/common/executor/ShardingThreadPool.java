package com.darcytech.demo.mysql.common.executor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.darcytech.demo.common.DxThreadPool;
import com.darcytech.demo.mysql.common.DataSourceRouter;
import com.darcytech.demo.mysql.common.JdbcServer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("StaticPseudoFunctionalStyleMethod")
@Slf4j
public class ShardingThreadPool extends DxThreadPool {

    private final ShardingDirectExecutor directExecutor;

    private final DataSourceRouter dataSourceRouter;

    public ShardingThreadPool(DataSourceRouter dataSourceRouter) {
        this.directExecutor = new ShardingDirectExecutor(dataSourceRouter.getDataSourceSelector());
        this.dataSourceRouter = dataSourceRouter;
    }

    public void dbExecute(Integer serverId, Runnable task) {
        execute(new DbRunnable(serverId, task));
    }

    public void dbTryExecute(Integer serverId, String taskKey, Runnable task) {
        tryExecute(taskKey, new DbRunnable(serverId, task));
    }

    public void dbRunAll(Integer serverId, Iterable<Runnable> tasks) {
        runAll(Iterables.transform(tasks, t -> new DbRunnable(serverId, t)));
    }

    public <T> List<T> dbCallAll(Integer serverId, Iterable<Callable<T>> tasks) {
        return callAll(Iterables.transform(tasks, t -> new DbCallable<>(serverId, t)));
    }

    public <T> List<T> dbFlatCallAll(Integer serverId, Iterable<Callable<Iterable<T>>> tasks) {
        return flatCallAll(Iterables.transform(tasks, t -> new DbCallable<>(serverId, t)));
    }

    public void stickDbExecute(Runnable task) {
        dbExecute(checkAndGetServerId(), task);
    }

    public void stickDbTryExecute(String taskKey, Runnable task) {
        dbTryExecute(checkAndGetServerId(), taskKey, task);
    }

    public void stickDbRunAll(Iterable<Runnable> tasks) {
        dbRunAll(checkAndGetServerId(), tasks);
    }

    public <T> List<T> stickDbCallAll(Iterable<Callable<T>> tasks) {
        return dbCallAll(checkAndGetServerId(), tasks);
    }

    public <T> List<T> stickDbFlatCallAll(Iterable<Callable<Iterable<T>>> tasks) {
        return dbFlatCallAll(checkAndGetServerId(), tasks);
    }

    public void runForEachDb(Runnable task) {
        runAll(createDbTasks(sid -> new DbRunnable(sid, task)));
    }

    public <T> List<T> callForEachDb(Callable<T> task) {
        return callAll(createDbTasks(sid -> new DbCallable<>(sid, task)));
    }

    public <T> List<T> flatCallForEachDb(Callable<Iterable<T>> task) {
        return flatCallAll(createDbTasks(sid -> new DbCallable<>(sid, task)));
    }

    @Nonnull
    private Integer checkAndGetServerId() {
        Integer serverId = dataSourceRouter.getDataSourceSelector().get();
        Preconditions.checkNotNull(serverId, "没有可以stick的数据源.");
        return serverId;
    }

    @RequiredArgsConstructor
    class DbRunnable implements Runnable {
        private final Integer serverId;
        private final Runnable task;

        @Override
        public void run() {
            directExecutor.dbDirectExecute(serverId, task);
        }
    }

    @RequiredArgsConstructor
    class DbCallable<T> implements Callable<T> {

        private final Integer serverId;
        private final Callable<T> task;

        @Override
        public T call() {
            return directExecutor.dbDirectExecute(serverId, task);
        }

    }

    private <R> List<R> createDbTasks(Function<Integer, R> dbTaskFactory) {
        return dataSourceRouter.getServers().stream()
                .map(JdbcServer::getId)
                .map(dbTaskFactory)
                .collect(Collectors.toList());
    }

}
