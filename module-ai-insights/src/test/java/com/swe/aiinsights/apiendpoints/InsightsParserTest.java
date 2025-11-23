package com.swe.aiinsights.apiendpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.swe.aiinsights.parser.InsightsParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for InsightsParser with 100% code coverage.
 */
class InsightsParserTest {

    // ==================== Valid Input Tests ====================

    @Test
    void testParse_ValidInput() throws JsonProcessingException {
        // Arrange
        String validInput = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]";

        // Act
        String result = InsightsParser.parse(validInput);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("2024-01-15T10:30:00Z"));
        assertTrue(result.contains("5.5"));
    }

    @Test
    void testParse_ValidInputWithMultipleEntries() throws JsonProcessingException {
        // Arrange
        String validInput = "[" +
                "{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}," +
                "{\"time\":\"2024-01-15T11:30:00Z\",\"sentiment\":-3.2}," +
                "{\"time\":\"2024-01-15T12:30:00Z\",\"sentiment\":8.9}" +
                "]";

        // Act
        String result = InsightsParser.parse(validInput);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("2024-01-15T10:30:00Z"));
        assertTrue(result.contains("5.5"));
        assertTrue(result.contains("-3.2"));
    }

    @Test
    void testParse_ValidInputWithJsonPrefix() throws JsonProcessingException {
        // Arrange
        String inputWithPrefix = "```json\n[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]\n```";

        // Act
        String result = InsightsParser.parse(inputWithPrefix);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
        assertFalse(result.contains("```"));
    }

    @Test
    void testParse_ValidInputWithBackticks() throws JsonProcessingException {
        // Arrange
        String inputWithBackticks = "```\n[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]\n```";

        // Act
        String result = InsightsParser.parse(inputWithBackticks);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
        assertFalse(result.contains("```"));
    }

    @Test
    void testParse_ValidInputWithOnlyStartingBackticks() throws JsonProcessingException {
        // Arrange
        String input = "```json\n[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("["));
    }

    @Test
    void testParse_ValidInputWithOnlyEndingBackticks() throws JsonProcessingException {
        // Arrange
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]\n```";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith("]"));
        assertFalse(result.contains("```"));
    }

    @Test
    void testParse_ValidInputWithWhitespace() throws JsonProcessingException {
        // Arrange
        String inputWithWhitespace = "  \n  [{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]  \n  ";

        // Act
        String result = InsightsParser.parse(inputWithWhitespace);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
    }

    // ==================== Sentiment Boundary Tests ====================

    @Test
    void testParse_SentimentAtMinBoundary() throws JsonProcessingException {
        // Arrange - sentiment = -10 (minimum valid value)
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":-10}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("-10"));
    }

    @Test
    void testParse_SentimentAtMaxBoundary() throws JsonProcessingException {
        // Arrange - sentiment = 10 (maximum valid value)
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":10}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("10"));
    }

    @Test
    void testParse_SentimentZero() throws JsonProcessingException {
        // Arrange
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":0}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("0"));
    }

    @Test
    void testParse_SentimentBelowMinBoundary() {
        // Arrange - sentiment = -11 (below minimum)
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":-11}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Sentiment out of range"));
        assertTrue(exception.getMessage().contains("-11"));
    }

    @Test
    void testParse_SentimentAboveMaxBoundary() {
        // Arrange - sentiment = 11 (above maximum)
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":11}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Sentiment out of range"));
        assertTrue(exception.getMessage().contains("11"));
    }

    // ==================== Time Format Tests ====================

    @Test
    void testParse_InvalidTimeFormat_NoT() {
        // Arrange - missing 'T' separator
        String input = "[{\"time\":\"2024-01-15 10:30:00Z\",\"sentiment\":5.5}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Invalid time format"));
    }

    @Test
    void testParse_InvalidTimeFormat_NoZ() {
        // Arrange - missing 'Z' at the end
        String input = "[{\"time\":\"2024-01-15T10:30:00\",\"sentiment\":5.5}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Invalid time format"));
    }

    @Test
    void testParse_InvalidTimeFormat_WrongDateFormat() {
        // Arrange - invalid date format
        String input = "[{\"time\":\"15-01-2024T10:30:00Z\",\"sentiment\":5.5}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Invalid time format"));
    }

    @Test
    void testParse_InvalidTimeFormat_ExtraCharacters() {
        // Arrange - extra characters in time
        String input = "[{\"time\":\"2024-01-15T10:30:00Z Extra\",\"sentiment\":5.5}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Invalid time format"));
    }

    @Test
    void testParse_NullTime() {
        // Arrange - null time value
        String input = "[{\"time\":null,\"sentiment\":5.5}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Invalid time format"));
    }

    // ==================== JSON Structure Tests ====================

    @Test
    void testParse_MissingOpeningBracket() {
        // Arrange
        String input = "{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Output not in the expected format"));
    }

    @Test
    void testParse_MissingClosingBracket() {
        // Arrange
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Output not in the expected format"));
    }

    @Test
    void testParse_EmptyArray() throws JsonProcessingException {
        // Arrange
        String input = "[]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertEquals("[]", result);
    }

    @Test
    void testParse_InvalidJSON() {
        // Arrange - malformed JSON
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Output not in the expected format"));
    }

    @Test
    void testParse_InvalidJSONStructure() {
        // Arrange - wrong field names
        String input = "[{\"timestamp\":\"2024-01-15T10:30:00Z\",\"value\":5.5}]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });


        assertNotNull(exception.getMessage());

    }

    // ==================== Edge Cases ====================

    @Test
    void testParse_MultipleEntriesWithOneBadSentiment() {
        // Arrange - second entry has invalid sentiment
        String input = "[" +
                "{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}," +
                "{\"time\":\"2024-01-15T11:30:00Z\",\"sentiment\":15}" +
                "]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Sentiment out of range"));
    }

    @Test
    void testParse_MultipleEntriesWithOneBadTime() {
        // Arrange - second entry has invalid time format
        String input = "[" +
                "{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}," +
                "{\"time\":\"invalid-time\",\"sentiment\":3.2}" +
                "]";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            InsightsParser.parse(input);
        });

        assertTrue(exception.getMessage().contains("Invalid time format"));
    }

    @Test
    void testParse_NegativeDecimalSentiment() throws JsonProcessingException {
        // Arrange
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":-9.99}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("-9.99"));
    }

    @Test
    void testParse_PositiveDecimalSentiment() throws JsonProcessingException {
        // Arrange
        String input = "[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":9.99}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("9.99"));
    }

    // ==================== SentimentEntry Class Tests ====================

    @Test
    void testSentimentEntry_GettersAndSetters() {
        // Arrange
        InsightsParser.SentimentEntry entry = new InsightsParser.SentimentEntry();

        // Act
        entry.setTime("2024-01-15T10:30:00Z");
        entry.setSentiment(7.5f);

        // Assert
        assertEquals("2024-01-15T10:30:00Z", entry.getTime());
        assertEquals(7.5f, entry.getSentiment(), 0.001);
    }

    @Test
    void testSentimentEntry_NullTime() {
        // Arrange
        InsightsParser.SentimentEntry entry = new InsightsParser.SentimentEntry();

        // Act
        entry.setTime(null);
        entry.setSentiment(5.0f);

        // Assert
        assertNull(entry.getTime());
        assertEquals(5.0f, entry.getSentiment(), 0.001);
    }

    @Test
    void testSentimentEntry_ZeroSentiment() {
        // Arrange
        InsightsParser.SentimentEntry entry = new InsightsParser.SentimentEntry();

        // Act
        entry.setTime("2024-01-15T10:30:00Z");
        entry.setSentiment(0.0f);

        // Assert
        assertEquals("2024-01-15T10:30:00Z", entry.getTime());
        assertEquals(0.0f, entry.getSentiment(), 0.001);
    }

    @Test
    void testSentimentEntry_NegativeSentiment() {
        // Arrange
        InsightsParser.SentimentEntry entry = new InsightsParser.SentimentEntry();

        // Act
        entry.setTime("2024-01-15T10:30:00Z");
        entry.setSentiment(-8.5f);

        // Assert
        assertEquals("2024-01-15T10:30:00Z", entry.getTime());
        assertEquals(-8.5f, entry.getSentiment(), 0.001);
    }

    // ==================== Complex Backtick Scenarios ====================

    @Test
    void testParse_BackticksWithJsonAndExtraWhitespace() throws JsonProcessingException {
        // Arrange
        String input = "```json\n\n  [{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]  \n\n```";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
        assertFalse(result.contains("```"));
        assertFalse(result.contains("json"));
    }

    @Test
    void testParse_OnlyStartingJsonBackticks() throws JsonProcessingException {
        // Arrange
        String input = "```json\n[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertFalse(result.contains("```"));
        assertFalse(result.contains("json"));
    }

    @Test
    void testParse_OnlyStartingPlainBackticks() throws JsonProcessingException {
        // Arrange
        String input = "```\n[{\"time\":\"2024-01-15T10:30:00Z\",\"sentiment\":5.5}]";

        // Act
        String result = InsightsParser.parse(input);

        // Assert
        assertNotNull(result);
        assertFalse(result.contains("```"));
    }
}