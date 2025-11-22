package com.swe.aiinsights.apiendpoints;

import com.swe.aiinsights.customexceptions.RateLimitException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RateLimitException with 100% code coverage.
 */
class RateLimitExceptionTest {

    @Test
    void testConstructor_WithMessage() {
        String errorMessage = "Rate limit exceeded";

        RateLimitException exception = new RateLimitException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testConstructor_WithEmptyMessage() {
        String errorMessage = "";

        RateLimitException exception = new RateLimitException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testConstructor_WithNullMessage() {
        RateLimitException exception = new RateLimitException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void testConstructor_WithLongMessage() {
        String errorMessage = "This is a very long error message that describes " +
                "in detail why the rate limit was exceeded and what the user should do next";

        RateLimitException exception = new RateLimitException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionIsInstanceOfIOException() {
        RateLimitException exception = new RateLimitException("Test");

        assertTrue(exception instanceof IOException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(RateLimitException.class, () -> {
            throw new RateLimitException("Rate limit reached");
        });
    }

    @Test
    void testExceptionCanBeCaught() {
        try {
            throw new RateLimitException("Test exception");
        } catch (RateLimitException e) {
            assertEquals("Test exception", e.getMessage());
        } catch (Exception e) {
            fail("Should have caught RateLimitException specifically");
        }
    }

    @Test
    void testExceptionCanBeCaughtAsIOException() {
        try {
            throw new RateLimitException("Test exception");
        } catch (IOException e) {
            assertTrue(e instanceof RateLimitException);
            assertEquals("Test exception", e.getMessage());
        }
    }

    @Test
    void testMultipleInstancesWithDifferentMessages() {
        RateLimitException exception1 = new RateLimitException("Message 1");
        RateLimitException exception2 = new RateLimitException("Message 2");
        RateLimitException exception3 = new RateLimitException("Message 3");

        assertNotEquals(exception1.getMessage(), exception2.getMessage());
        assertNotEquals(exception2.getMessage(), exception3.getMessage());
        assertNotEquals(exception1.getMessage(), exception3.getMessage());
    }

    @Test
    void testExceptionWithSpecialCharacters() {
        String messageWithSpecialChars = "Rate limit: 100 req/min exceeded! @#$%^&*()";

        RateLimitException exception = new RateLimitException(messageWithSpecialChars);

        assertEquals(messageWithSpecialChars, exception.getMessage());
    }

    @Test
    void testExceptionStackTrace() {
        RateLimitException exception = new RateLimitException("Test");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void testExceptionToString() {
        String message = "Rate limit exceeded";
        RateLimitException exception = new RateLimitException(message);

        String toString = exception.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("RateLimitException"));
        assertTrue(toString.contains(message));
    }
}