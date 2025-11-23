/*
 * -----------------------------------------------------------------------------
 *  File: InsightsParserTest.java
 *  Owner: Nandhana Sunil
 *  Roll Number : 112201008
 *  Module : com.swe.aiinsights
 * -----------------------------------------------------------------------------
 */

package com.swe.aiinsights;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swe.aiinsights.parser.InsightsParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for InsightsParser with 100% code coverage.
 */
class InsightsParserTest {

    // Valid Input
    @Test
    void testParseValidInput() throws JsonProcessingException {
        final String validInput = "```json["
                + "{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":5.5},"
                + "{\"time\":\"2025-01-15T11:30:00Z\",\"sentiment\":-3.2},"
                + "{\"time\":\"2025-01-15T12:30:00Z\",\"sentiment\":8.9}"
                + "]```";

        final String result = InsightsParser.parse(validInput);
        assertNotNull(result);
        assertTrue(result.contains("2025-01-15T10:30:00Z"));
        assertTrue(result.contains("5.5"));
        assertTrue(result.contains("-3.2"));
    }

    // response do not start with [
    @Test
    void testInvalidStart() throws JsonProcessingException {
        final String invalidInput = "```json"
                + "{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":5.5},"
                + "{\"time\":\"2025-01-15T11:30:00Z\",\"sentiment\":-3.2},"
                + "{\"time\":\"2025-01-15T12:30:00Z\",\"sentiment\":8.9}"
                + "]```";

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(invalidInput);
        });
        assertNotNull(exception.getMessage());
    }

    // response starting with ```
    @Test
    void testParseValidInputTicks() throws JsonProcessingException {
        final String validInput = "```["
                + "{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":5.5},"
                + "{\"time\":\"2025-01-15T11:30:00Z\",\"sentiment\":-3.2},"
                + "{\"time\":\"2025-01-15T12:30:00Z\",\"sentiment\":8.9}"
                + "]```";

        final String result = InsightsParser.parse(validInput);
        assertNotNull(result);
        assertTrue(result.contains("2025-01-15T10:30:00Z"));
        assertTrue(result.contains("5.5"));
        assertTrue(result.contains("-3.2"));
    }

    //valid tests starting with ```
    @Test
    void testInvalidEnd() throws JsonProcessingException {
        final String invalidInput = "```json["
                + "{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":5.5},"
                + "{\"time\":\"2025-01-15T11:30:00Z\",\"sentiment\":-3.2},"
                + "{\"time\":\"2025-01-15T12:30:00Z\",\"sentiment\":8.9}"
                + "```";

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(invalidInput);
        });
        assertNotNull(exception.getMessage());
    }

    // response do not have sentiment field
    @Test
    void testInvalidSentimentField() throws JsonProcessingException {
        final String invalidInput = "```json["
                + "{\"time\":\"2025-01-15T10:30:00Z\"},"
                + "{\"time\":\"2025-01-15T11:30:00Z\",\"sentiment\":-3.2},"
                + "{\"time\":\"2025-01-15T12:30:00Z\",\"sentiment\":8.9}"
                + "```";

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(invalidInput);
        });
        assertNotNull(exception.getMessage());
    }

    // response do not have time field
    @Test
    void testInvalidTimeField() throws JsonProcessingException {
        final String invalidInput = "```json["
                + "{\"sentiment\":-3.2},"
                + "{\"time\":\"2025-01-15T12:30:00Z\",\"sentiment\":8.9}"
                + "```";

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(invalidInput);
        });
        assertNotNull(exception.getMessage());
    }

    // check if the json format is valid
    @Test
    void testParseInvalidJson() {
        final String input = "[{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":5.5";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });
        assertTrue(exception.getMessage().contains("Output not in the expected format"));
    }

    @Test
    void testParseInvalidTimeFormat() {
        final String input = "[{\"time\":\"2025-01-15 10:30:00\",\"sentiment\":5.5}]";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });
        assertTrue(exception.getMessage().contains("Invalid time format"));
    }

    @Test
    void testParseSentimentBelowMinBoundary() {
        final String input = "[{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":-11}]";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });
        assertTrue(exception.getMessage().contains("Sentiment out of range"));
    }

    @Test
    void testParseSentimentAboveMaxBoundary() {
        final String input = "[{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":11}]";
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });
        assertTrue(exception.getMessage().contains("Sentiment out of range"));
    }

    @Test
    void testSentimentEntryGetAndSet() {
        final InsightsParser.SentimentEntry entry = new InsightsParser.SentimentEntry();
        entry.setTime("2025-01-15T10:30:00Z");
        entry.setSentiment(7.5f);
        assertEquals("2025-01-15T10:30:00Z", entry.getTime());
        assertEquals(7.5f, entry.getSentiment(), 0.001);
    }

    @Test
    void testSentimentEntryNullTime() {
        final InsightsParser.SentimentEntry entry = new InsightsParser.SentimentEntry();
        entry.setTime(null);
        entry.setSentiment(5.0f);
        assertNull(entry.getTime());
        assertEquals(5.0f, entry.getSentiment(), 0.001);
    }

    @Test
    void testListInvalid()throws JsonProcessingException {
        final String input = "```json["
                + "{\"time\":\"2025-01-15T10:30:00Z\",\"sentiment\":5.5}"
                + "{\"time\":\"2025-01-15T11:30:00Z\",\"sentiment\":-3.2}"
                + "{\"time\":\"2025-01-15T12:30:00Z\",\"sentiment\":8.9}"
                + "]```";

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });
        assertTrue(exception.getMessage().contains("Output not in the expected format"));
    }
}