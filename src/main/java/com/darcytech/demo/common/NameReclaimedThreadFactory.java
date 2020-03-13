package com.darcytech.demo.common;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NameReclaimedThreadFactory extends CustomizableThreadFactory {

    private final PriorityQueue<Integer> reclaimedQueue = new PriorityQueue<>();

    private final AtomicInteger allocateCount = new AtomicInteger(0);

    public NameReclaimedThreadFactory(String threadNamePrefix) {
        super(threadNamePrefix);
    }

    @Override
    public Thread createThread(Runnable runnable) {
        Integer seq = nextSeq();
        Runnable reclaimRunnable = () -> {
            try {
                runnable.run();
            } finally {
                reclaim(seq);
            }
        };

        Thread thread = new Thread(getThreadGroup(), reclaimRunnable, getThreadNamePrefix() + seq);
        thread.setPriority(getThreadPriority());
        thread.setDaemon(isDaemon());
        return thread;
    }

    protected Integer nextSeq() {
        Integer seq;
        synchronized (this) {
            seq = reclaimedQueue.poll();
        }
        if (seq != null) {
            return seq;
        }
        return allocateCount.incrementAndGet();
    }

    protected synchronized void reclaim(Integer threadId) {
        if (reclaimedQueue.size() > 2000) {
            log.warn("skip! 2000 seq id have been reclaimed, threadNamePrefix: " + getThreadNamePrefix());
            return;
        }
        reclaimedQueue.offer(threadId);
    }

}

