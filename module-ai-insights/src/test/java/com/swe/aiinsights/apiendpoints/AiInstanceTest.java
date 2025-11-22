package com.swe.aiinsights.aiinstance;

import com.swe.aiinsights.apiendpoints.AiClientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for AiInstance with 100% code coverage.
 * Tests singleton pattern, thread safety, and error handling.
 */
@ExtendWith(MockitoExtension.class)
class AiInstanceTest {

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton instance before each test
        resetSingleton();
    }

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
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
            AiClientService instance1 = AiInstance.getInstance();
            AiClientService instance2 = AiInstance.getInstance();

            assertNotNull(instance1);
            assertNotNull(instance2);
            assertSame(instance1, instance2, "getInstance should return the same instance");

            // Verify AiClientService was only constructed once
            assertEquals(1, mockedConstruction.constructed().size());
        }
    }

    @Test
    void testGetInstance_FirstCallCreatesInstance() throws Exception {
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
            AiClientService instance = AiInstance.getInstance();

            assertNotNull(instance);
            assertEquals(1, mockedConstruction.constructed().size());
        }
    }

    @Test
    void testGetInstance_MultipleCallsReturnSameInstance() throws Exception {
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
            AiClientService instance1 = AiInstance.getInstance();
            AiClientService instance2 = AiInstance.getInstance();
            AiClientService instance3 = AiInstance.getInstance();
            AiClientService instance4 = AiInstance.getInstance();

            assertSame(instance1, instance2);
            assertSame(instance2, instance3);
            assertSame(instance3, instance4);

            // Verify only one instance was created
            assertEquals(1, mockedConstruction.constructed().size());
        }
    }

    // ==================== Thread Safety Tests ====================

    @Test
    void testGetInstance_ThreadSafety_ConcurrentAccess() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<AiClientService>> futures = new ArrayList<>();

        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
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
                instances.add(future.get(5, TimeUnit.SECONDS));
            }

            // Verify all threads got the same instance
            AiClientService firstInstance = instances.get(0);
            for (AiClientService instance : instances) {
                assertSame(firstInstance, instance, "All threads should get the same singleton instance");
            }

            // Verify only one instance was created despite concurrent access
            assertEquals(1, mockedConstruction.constructed().size(),
                    "Only one instance should be created despite concurrent calls");

        } finally {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void testGetInstance_ThreadSafety_HighConcurrency() throws Exception {
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        List<Future<AiClientService>> futures = new ArrayList<>();

        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
            for (int i = 0; i < threadCount; i++) {
                Future<AiClientService> future = executorService.submit(() -> {
                    try {
                        barrier.await(); // Synchronize all threads
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                    return AiInstance.getInstance();
                });
                futures.add(future);
            }

            // Collect all instances
            List<AiClientService> instances = new ArrayList<>();
            for (Future<AiClientService> future : futures) {
                instances.add(future.get(10, TimeUnit.SECONDS));
            }

            // Verify singleton property
            AiClientService firstInstance = instances.get(0);
            for (AiClientService instance : instances) {
                assertSame(firstInstance, instance);
            }

            // Only one instance should be created
            assertEquals(1, mockedConstruction.constructed().size());

        } finally {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    // ==================== Error Handling Tests ====================

    @Test
    void testGetInstance_ConstructorThrowsException() throws Exception {
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(
                AiClientService.class,
                (mock, context) -> {
                    throw new RuntimeException("Simulated initialization failure");
                })) {

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                AiInstance.getInstance();
            });

            assertTrue(exception.getMessage().contains("AI Service Initialization Failed"));
            assertNotNull(exception.getCause());
            assertTrue(exception.getCause().getMessage().contains("Simulated initialization failure"));
        }
    }

    @Test
    void testGetInstance_AfterFailedInitialization_CanRetry() throws Exception {
        // First attempt - fail
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(
                AiClientService.class,
                (mock, context) -> {
                    throw new RuntimeException("First attempt fails");
                })) {

            assertThrows(RuntimeException.class, () -> {
                AiInstance.getInstance();
            });
        }

        // Reset singleton for retry
        resetSingleton();

        // Second attempt - succeed
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
            AiClientService instance = AiInstance.getInstance();
            assertNotNull(instance);
        }
    }

    @Test
    void testGetInstance_ExceptionContainsOriginalCause() throws Exception {
        String originalMessage = "Database connection failed";

        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(
                AiClientService.class,
                (mock, context) -> {
                    throw new IllegalStateException(originalMessage);
                })) {

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                AiInstance.getInstance();
            });

            assertEquals("AI Service Initialization Failed", exception.getMessage());
            assertNotNull(exception.getCause());
            assertEquals(originalMessage, exception.getCause().getMessage());
            assertTrue(exception.getCause() instanceof IllegalStateException);
        }
    }

    // ==================== Double-Checked Locking Tests ====================

    @Test
    void testGetInstance_DoubleCheckedLocking_LocalReference() throws Exception {
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
            // First call - creates instance
            AiClientService instance1 = AiInstance.getInstance();
            assertNotNull(instance1);

            // Second call - should use local reference optimization
            AiClientService instance2 = AiInstance.getInstance();
            assertSame(instance1, instance2);

            // Verify only one construction
            assertEquals(1, mockedConstruction.constructed().size());
        }
    }

    @Test
    void testGetInstance_NoUnnecessarySynchronization() throws Exception {
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
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
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
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

            // Only one instance created
            assertEquals(1, mockedConstruction.constructed().size());
        }
    }

    @Test
    void testGetInstance_ReturnsNonNull() throws Exception {
        try (MockedConstruction<AiClientService> mockedConstruction = mockConstruction(AiClientService.class)) {
            AiClientService instance = AiInstance.getInstance();
            assertNotNull(instance, "getInstance should never return null");
        }
    }
}