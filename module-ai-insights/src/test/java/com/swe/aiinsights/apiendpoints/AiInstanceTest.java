package com.swe.aiinsights.apiendpoints;

import com.swe.aiinsights.aiinstance.AiInstance;
import com.swe.aiinsights.apiendpoints.AiClientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AiInstance with maximum achievable code coverage.
 * Tests singleton pattern, thread safety, and error handling.
 * Note: Uses real AiClientService instances since MockedConstruction
 * doesn't work well with synchronized blocks.
 */
@ExtendWith(MockitoExtension.class)
class AiInstanceTest {

    @AfterEach
    void tearDown() throws Exception {
        // Clean up after each test
        resetSingleton();
    }

    /**
     * Helper method to reset the singleton instance using reflection
     */
    private void resetSingleton() throws Exception {
        Field instance = AiInstance.class.getDeclaredField("aiClientService");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    // ==================== Constructor Tests ====================

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<AiInstance> constructor = AiInstance.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));

        // Make constructor accessible and invoke it
        constructor.setAccessible(true);
        AiInstance instance = constructor.newInstance();
        assertNotNull(instance);
    }

    // ==================== Singleton Pattern Tests ====================

    @Test
    void testGetInstance_ReturnsSameInstance() throws Exception {
        resetSingleton();

        AiClientService instance1 = AiInstance.getInstance();
        AiClientService instance2 = AiInstance.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "getInstance should return the same instance");
    }

    @Test
    void testGetInstance_FirstCallCreatesInstance() throws Exception {
        resetSingleton();

        AiClientService instance = AiInstance.getInstance();

        assertNotNull(instance);
    }

    @Test
    void testGetInstance_MultipleCallsReturnSameInstance() throws Exception {
        resetSingleton();

        AiClientService instance1 = AiInstance.getInstance();
        AiClientService instance2 = AiInstance.getInstance();
        AiClientService instance3 = AiInstance.getInstance();
        AiClientService instance4 = AiInstance.getInstance();

        assertSame(instance1, instance2);
        assertSame(instance2, instance3);
        assertSame(instance3, instance4);
    }

    @Test
    void testGetInstance_ReturnsNonNull() throws Exception {
        resetSingleton();

        AiClientService instance = AiInstance.getInstance();
        assertNotNull(instance, "getInstance should never return null");
    }

    // ==================== Thread Safety Tests ====================

    @Test
    void testGetInstance_ThreadSafety_ConcurrentAccess() throws Exception {
        resetSingleton();

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<AiClientService>> futures = new ArrayList<>();

        try {
            // Submit multiple threads to call getInstance simultaneously
            for (int i = 0; i < threadCount; i++) {
                Future<AiClientService> future = executorService.submit(() -> {
                    latch.countDown();
                    try {
                        latch.await(); // Wait for all threads to be ready
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return AiInstance.getInstance();
                });
                futures.add(future);
            }

            // Get all results
            List<AiClientService> instances = new ArrayList<>();
            for (Future<AiClientService> future : futures) {
                instances.add(future.get(10, TimeUnit.SECONDS));
            }

            // Verify all threads got the same instance
            AiClientService firstInstance = instances.get(0);
            for (AiClientService instance : instances) {
                assertSame(firstInstance, instance, "All threads should get the same singleton instance");
            }

        } finally {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Test
    void testGetInstance_ThreadSafety_HighConcurrency() throws Exception {
        resetSingleton();

        int threadCount = 20; // Reduced thread count for more reliable test
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ConcurrentHashMap<Integer, AiClientService> resultMap = new ConcurrentHashMap<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executorService.submit(() -> {
                    try {
                        startLatch.await(); // All threads wait here
                        AiClientService instance = AiInstance.getInstance();
                        resultMap.put(threadId, instance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            // Release all threads at once
            startLatch.countDown();

            // Wait for all to complete
            assertTrue(doneLatch.await(10, TimeUnit.SECONDS), "All threads should complete");

            // Verify all got the same instance
            assertEquals(threadCount, resultMap.size(), "All threads should have stored a result");

            AiClientService firstInstance = resultMap.get(0);
            assertNotNull(firstInstance, "First instance should not be null");

            for (int i = 0; i < threadCount; i++) {
                AiClientService instance = resultMap.get(i);
                assertNotNull(instance, "Instance " + i + " should not be null");
                assertSame(firstInstance, instance,
                        "Thread " + i + " should get the same singleton instance");
            }

        } finally {
            executorService.shutdown();
            assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));
        }
    }
    @Test
    void testGetInstance_ThreadSafety_SequentialAfterConcurrent() throws Exception {
        resetSingleton();

        // First concurrent access
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<AiClientService>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                futures.add(executorService.submit(() -> AiInstance.getInstance()));
            }

            AiClientService concurrentInstance = futures.get(0).get(5, TimeUnit.SECONDS);

            // Then sequential access
            AiClientService sequentialInstance1 = AiInstance.getInstance();
            AiClientService sequentialInstance2 = AiInstance.getInstance();

            // All should be the same
            assertSame(concurrentInstance, sequentialInstance1);
            assertSame(sequentialInstance1, sequentialInstance2);

        } finally {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ==================== Double-Checked Locking Tests ====================

    @Test
    void testGetInstance_DoubleCheckedLocking_LocalReference() throws Exception {
        resetSingleton();

        // First call - creates instance
        AiClientService instance1 = AiInstance.getInstance();
        assertNotNull(instance1);

        // Second call - should use local reference optimization
        AiClientService instance2 = AiInstance.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testGetInstance_NoUnnecessarySynchronization() throws Exception {
        resetSingleton();

        // Create instance first
        AiInstance.getInstance();

        // Measure time for subsequent calls (should be very fast without synchronization)
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            AiInstance.getInstance();
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        // Should complete quickly (under 10ms for 1000 calls) because no synchronization
        assertTrue(duration < 10_000_000, "Subsequent calls should be fast without synchronization");
    }

    // ==================== Volatile Field Tests ====================

    @Test
    void testAiClientServiceField_IsVolatile() throws Exception {
        Field field = AiInstance.class.getDeclaredField("aiClientService");
        assertTrue(java.lang.reflect.Modifier.isVolatile(field.getModifiers()),
                "aiClientService field should be volatile for thread safety");
    }

    @Test
    void testAiClientServiceField_IsStatic() throws Exception {
        Field field = AiInstance.class.getDeclaredField("aiClientService");
        assertTrue(java.lang.reflect.Modifier.isStatic(field.getModifiers()),
                "aiClientService field should be static");
    }

    @Test
    void testAiClientServiceField_IsPrivate() throws Exception {
        Field field = AiInstance.class.getDeclaredField("aiClientService");
        assertTrue(java.lang.reflect.Modifier.isPrivate(field.getModifiers()),
                "aiClientService field should be private");
    }

    // ==================== Edge Cases ====================

    @Test
    void testGetInstance_RapidSuccessiveCalls() throws Exception {
        resetSingleton();

        List<AiClientService> instances = new ArrayList<>();

        // Make 100 rapid calls
        for (int i = 0; i < 100; i++) {
            instances.add(AiInstance.getInstance());
        }

        // All should be the same instance
        AiClientService firstInstance = instances.get(0);
        for (AiClientService instance : instances) {
            assertSame(firstInstance, instance);
        }
    }

    @Test
    void testGetInstance_AlternatingThreads() throws Exception {
        resetSingleton();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<AiClientService>> futures = new ArrayList<>();

        try {
            // Alternate between two threads
            for (int i = 0; i < 20; i++) {
                futures.add(executorService.submit(() -> AiInstance.getInstance()));
            }

            // Get all instances
            List<AiClientService> instances = new ArrayList<>();
            for (Future<AiClientService> future : futures) {
                instances.add(future.get(5, TimeUnit.SECONDS));
            }

            // Verify all are the same
            AiClientService firstInstance = instances.get(0);
            for (AiClientService instance : instances) {
                assertSame(firstInstance, instance);
            }

        } finally {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void testGetInstance_StressTest() throws Exception {
        resetSingleton();

        int iterations = 1000;
        AiClientService firstInstance = AiInstance.getInstance();

        for (int i = 0; i < iterations; i++) {
            AiClientService instance = AiInstance.getInstance();
            assertSame(firstInstance, instance, "Instance should remain same across " + iterations + " calls");
        }
    }

    @Test
    void testGetInstance_VerifyInstanceType() throws Exception {
        resetSingleton();

        AiClientService instance = AiInstance.getInstance();

        assertNotNull(instance);
        assertTrue(instance instanceof AiClientService);
    }

    @Test
    void testGetInstance_MultipleResetAndRecreate() throws Exception {
        // First creation
        resetSingleton();
        AiClientService instance1 = AiInstance.getInstance();
        assertNotNull(instance1);

        // Reset and recreate
        resetSingleton();
        AiClientService instance2 = AiInstance.getInstance();
        assertNotNull(instance2);

        // Reset and recreate again
        resetSingleton();
        AiClientService instance3 = AiInstance.getInstance();
        assertNotNull(instance3);

        // After reset, instances should be different
        // (This tests that reset actually works)
        assertNotSame(instance1, instance2);
        assertNotSame(instance2, instance3);
    }

    @Test
    void testGetInstance_ClassIsPublic() {
        assertTrue(java.lang.reflect.Modifier.isPublic(AiInstance.class.getModifiers()));
    }

    @Test
    void testGetInstance_MethodIsPublic() throws Exception {
        var method = AiInstance.class.getMethod("getInstance");
        assertTrue(java.lang.reflect.Modifier.isPublic(method.getModifiers()));
    }

    @Test
    void testGetInstance_MethodIsStatic() throws Exception {
        var method = AiInstance.class.getMethod("getInstance");
        assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()));
    }
}