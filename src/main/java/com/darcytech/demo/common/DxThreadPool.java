package com.darcytech.demo.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Iterables;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DxThreadPool extends ThreadPoolTaskExecutor {

    private ConcurrentMap<Object, Runnable> runningTasks = new ConcurrentHashMap<>();

    @Override
    public void execute(Runnable task) {
        super.execute(wrapErrorLogging(task));
    }

    public void runAll(Iterable<Runnable> tasks) {
        List<Future<?>> futures = new ArrayList<>(Iterables.size(tasks));
        for (Runnable task : tasks) {
            futures.add(submit(task));
        }
        waitAllDone(futures, o -> {});
    }

    public <T> List<T> callAll(Iterable<Callable<T>> tasks) {
        List<Future<T>> futures = new ArrayList<>(Iterables.size(tasks));
        for (Callable<T> task : tasks) {
            futures.add(submit(task));
        }
        List<T> result = new ArrayList<>(futures.size());
        waitAllDone(futures, result::add);
        return result;
    }

    public <T> List<T> flatCallAll(Iterable<Callable<Iterable<T>>> tasks) {
        List<Iterable<T>> results = callAll(tasks);
        List<T> flatResults = new ArrayList<>();
        for (Iterable<T> each : results) {
            Iterables.addAll(flatResults, each);
        }
        return flatResults;
    }

    public void tryExecute(Long taskKey, Runnable task) {
        tryExecuteInternal(taskKey, task);
    }

    public void tryExecute(Integer taskKey, Runnable task) {
        tryExecuteInternal(taskKey, task);
    }

    public void tryExecute(String taskKey, Runnable task) {
        tryExecuteInternal(taskKey, task);
    }

    @Override
    protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        if (threadFactory == this) {
            threadFactory = new NameReclaimedThreadFactory(getThreadNamePrefix());
        }
        return super.initializeExecutor(threadFactory, rejectedExecutionHandler);
    }

    private void tryExecuteInternal(Object taskKey, Runnable task) {
        if (runningTasks.containsKey(taskKey)) {
            logTaskRunning(taskKey);
            return;
        }
        execute(() -> {
            if (runningTasks.putIfAbsent(taskKey, task) == null) {
                try {
                    task.run();
                }finally {
                    runningTasks.remove(taskKey);
                }
            } else {
                logTaskRunning(taskKey);
            }
        });
    }

    private static void logTaskRunning(Object taskKey) {
        log.info("skip running task {}, as same key task has already been running.", taskKey);
    }

    @SneakyThrows
    private static <T> void waitAllDone(List<? extends Future<? extends T>> futures, Consumer<T> consumer) {
        Throwable ex = null;
        for (Future<? extends T> f : futures) {
            try {
                consumer.accept(collect(f));
            } catch (Throwable e) {
                Throwable newEx = e.getCause() != null ? e.getCause() : e;
                if (ex != null && hasDifferentStackTrace(ex, newEx)) {
                    log.error("task execution exception", ex);
                }
                ex = newEx;
            }
        }
        if (ex != null) {
            throw ex;
        }
    }

    @SneakyThrows
    private static <T> T collect(Future<T> future) {
        while (true) {
            try {
                return future.get();
            } catch (InterruptedException e) {
                // sleep 1 millisecond, to prevent infinite loop causing cpu 100%
                Thread.sleep(1);
            }
        }
    }

    private static boolean hasDifferentStackTrace(Throwable t1, Throwable t2) {
        return !t1.getStackTrace()[0].equals(t2.getStackTrace()[0]);
    }

    public static Runnable wrapErrorLogging(Runnable runnable) {
        return new ErrorLoggingRunnable(runnable);
    }

    private static class ErrorLoggingRunnable implements Runnable {

        private Runnable task;

        public ErrorLoggingRunnable(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                task.run();
            } catch (Throwable ex) {
                log.error("Unexpected error occurred.", ex);
            }
        }
    }

}
