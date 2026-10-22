package com.han.community.common;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentExecutor {

    private ConcurrentExecutor() {}

    public record Result(int success, List<Throwable> failures) {}

    public static Result run(int threadCount, Runnable task) throws InterruptedException {

        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger();
        List<Throwable> failures = new CopyOnWriteArrayList<>();

        try(ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {

            for(int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        ready.countDown();
                        start.await();
                        task.run();
                        success.incrementAndGet();
                    } catch(Throwable t) {
                        failures.add(t);
                    } finally {
                        done.countDown();
                    }
                });
            }
            ready.await();
            start.countDown();

            if(!done.await(30, TimeUnit.SECONDS)) {
                throw new AssertionError("30초 안에 종료되지 않음");
            }
        }

        return new Result(success.get(), List.copyOf(failures));
    }
}
