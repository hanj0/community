package com.han.community.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Testcontainers
public abstract class IntegrationTestSupport {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16").withReuse(true);

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    protected ConcurrentResult runConcurrently(int threadCount, Runnable task) throws InterruptedException {

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threadCount);

        AtomicInteger success   = new AtomicInteger();
        AtomicInteger duplicate = new AtomicInteger();
        AtomicInteger otherFail = new AtomicInteger();

        try(ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++) {

                executor.submit(() -> {
                    try {
                        start.await();
                        task.run();
                        success.incrementAndGet();
                    } catch(DataIntegrityViolationException e) {
                        duplicate.incrementAndGet();
                    } catch(RuntimeException e) {
                        e.printStackTrace();
                        otherFail.incrementAndGet();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        otherFail.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }
            start.countDown();
            done.await();
        }

        return new ConcurrentResult(
                success.get(),
                duplicate.get(),
                otherFail.get()
        );
    }

    protected record ConcurrentResult(
            int success,
            int duplicate,
            int otherFail
    ) {}

}
