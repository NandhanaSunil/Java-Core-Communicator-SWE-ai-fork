/*
 * -----------------------------------------------------------------------------
 *  File: RateLimitExceptionTest.java
 *  Owner: Nandhana Sunil
 *  Roll Number : 112201008
 *  Module : com.swe.aiinsights
 * -----------------------------------------------------------------------------
 */

package com.swe.aiinsights;

import com.swe.aiinsights.customexceptions.RateLimitException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Rate limit exceeded";

        RateLimitException exception = new RateLimitException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testConstructorWithNull() {
        RateLimitException exception = new RateLimitException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

}