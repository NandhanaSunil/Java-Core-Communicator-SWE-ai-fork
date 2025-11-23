package com.swe.aiinsights.apiendpoints;

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
    void testAiExecutor_ReturnsExecutor() {
        // Act
        Executor executor = AsyncConfig.aiExecutor();

        // Assert
        assertNotNull(executor);
    }

    @Test
    void testAiExecutor_ReturnsThreadPoolExecutor() {
        // Act
        Executor executor = AsyncConfig.aiExecutor();

        // Assert
        assertTrue(executor instanceof ThreadPoolExecutor);
    }

    @Test
    void testAiExecutor_CanExecuteTasks() throws InterruptedException {
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
    void testAiExecutor_MultipleTasks() throws InterruptedException {
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

    @Test
    void testAiExecutor_ThreadNaming() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger threadNameCheck = new AtomicInteger(0);

        // Act
        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            if (threadName.startsWith("AI-Worker-")) {
                threadNameCheck.incrementAndGet();
            }
            latch.countDown();
        });

        // Wait for task to complete
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed);
        assertEquals(1, threadNameCheck.get(), "Thread name should start with 'AI-Worker-'");
    }

    @Test
    void testAiExecutor_ConcurrentExecution() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        int taskCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(taskCount);
        AtomicInteger concurrentCounter = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);

        // Act
        for (int i = 0; i < taskCount; i++) {
            executor.execute(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    int current = concurrentCounter.incrementAndGet();

                    // Track max concurrent
                    synchronized (maxConcurrent) {
                        if (current > maxConcurrent.get()) {
                            maxConcurrent.set(current);
                        }
                    }

                    Thread.sleep(100); // Simulate work
                    concurrentCounter.decrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Release all threads
        boolean completed = endLatch.await(10, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed);
        assertTrue(maxConcurrent.get() > 0, "Should have concurrent execution");
    }

    @Test
    void testAiExecutor_HandlesExceptions() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);

        // Act - Submit task that throws exception
        executor.execute(() -> {
            try {
                throw new RuntimeException("Test exception");
            } finally {
                latch.countDown();
            }
        });

        // Submit task that succeeds
        executor.execute(() -> {
            successCount.incrementAndGet();
            latch.countDown();
        });

        // Wait for both tasks
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        // Assert - Exception in one task shouldn't affect others
        assertTrue(completed);
        assertEquals(1, successCount.get());
    }

    @Test
    void testAiExecutor_MultipleCallsReturnDifferentInstances() {
        // Act
        Executor executor1 = AsyncConfig.aiExecutor();
        Executor executor2 = AsyncConfig.aiExecutor();

        // Assert - Each call creates a new instance
        assertNotSame(executor1, executor2);
    }

    @Test
    void testAiExecutor_ThreadPoolProperties() {
        // Act
        Executor executor = AsyncConfig.aiExecutor();

        // Assert
        assertTrue(executor instanceof ThreadPoolExecutor);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;

        // Verify it's configured (not checking exact values as they might change)
        assertTrue(tpe.getCorePoolSize() > 0);
        assertTrue(tpe.getMaximumPoolSize() >= tpe.getCorePoolSize());
    }

    @Test
    void testAiExecutor_CanExecuteAfterCompletion() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        // Act - Execute first task
        executor.execute(() -> {
            counter.incrementAndGet();
            latch1.countDown();
        });
        latch1.await(5, TimeUnit.SECONDS);

        // Execute second task after first completes
        executor.execute(() -> {
            counter.incrementAndGet();
            latch2.countDown();
        });
        latch2.await(5, TimeUnit.SECONDS);

        // Assert
        assertEquals(2, counter.get());
    }

    @Test
    void testAiExecutor_LongRunningTask() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger taskCompleted = new AtomicInteger(0);

        // Act - Submit a longer task
        executor.execute(() -> {
            try {
                Thread.sleep(1000); // 1 second task
                taskCompleted.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        // Wait with sufficient timeout
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed);
        assertEquals(1, taskCompleted.get());
    }

    @Test
    void testAiExecutor_RapidTaskSubmission() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        int taskCount = 50;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger counter = new AtomicInteger(0);

        // Act - Rapidly submit many tasks
        for (int i = 0; i < taskCount; i++) {
            executor.execute(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
        }

        // Wait for all tasks
        boolean completed = latch.await(30, TimeUnit.SECONDS);

        // Assert
        assertTrue(completed);
        assertEquals(taskCount, counter.get());
    }

    @Test
    void testAiExecutor_TaskOrderNotGuaranteed() throws InterruptedException {
        // Arrange
        Executor executor = AsyncConfig.aiExecutor();
        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger executionOrder = new AtomicInteger(0);
        int[] order = new int[taskCount];

        // Act - Submit tasks with identifiers
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            executor.execute(() -> {
                int position = executionOrder.getAndIncrement();
                order[position] = taskId;
                latch.countDown();
            });
        }

        // Wait for all tasks
        boolean completed = latch.await(10, TimeUnit.SECONDS);

        // Assert - Just verify all tasks executed
        assertTrue(completed);
        assertEquals(taskCount, executionOrder.get());
    }
}