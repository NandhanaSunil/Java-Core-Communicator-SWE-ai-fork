package com.swe.aiinsights;

import com.swe.aiinsights.configu.AsyncConfig;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AsyncConfig with 100% code coverage.
 */
class AsyncConfigTest {

    // ==================== Basic Tests ====================

    @Test
    void testAiExecutorReturnsExecutor() {
        // Act
        Executor executor = AsyncConfig.aiExecutor();

        // Assert
        assertTrue(executor instanceof ThreadPoolExecutor);
    }



    @Test
    void testAiExecutorCanExecuteTasks() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        // Act
        executor.execute(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        // Wait for task to complete
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed);
        assertEquals(1, counter.get());
    }

    @Test
    void testAiExecutorMultipleTasks() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger counter = new AtomicInteger(0);

        // Act
        for (int i = 0; i < taskCount; i++) {
            executor.execute(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
        }

        // Wait for all tasks to complete
        boolean completed = latch.await(10, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed);
        assertEquals(taskCount, counter.get());
    }
}