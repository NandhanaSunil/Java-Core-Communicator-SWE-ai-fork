/**
 * Class that creates a custom Exception when rate limit of cloud LLM is reached.
 * @author Nandhana Sunil
 * @version 1.0.0
 * @since 1.0.0
 */

package com.swe.aiinsights.customexceptions;

import java.io.IOException;

/**
 * RateLimitException is a type of IOException.
 */
public class RateLimitException extends IOException {
    public RateLimitException(String message) {
        super(message);
    }
}