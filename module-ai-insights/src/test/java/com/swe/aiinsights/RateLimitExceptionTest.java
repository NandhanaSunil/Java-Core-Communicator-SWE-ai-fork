package com.swe.aiinsights;

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

}