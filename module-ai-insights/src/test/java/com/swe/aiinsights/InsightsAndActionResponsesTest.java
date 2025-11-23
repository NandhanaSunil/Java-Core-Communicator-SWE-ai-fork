package com.swe.aiinsights;

import com.swe.aiinsights.response.ActionItemsResponse;
import com.swe.aiinsights.response.AiResponse;
import com.swe.aiinsights.response.InsightsResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for InsightsResponse and ActionItemsResponse
 * to achieve 100% coverage on all Response classes.
 */
class InsightsAndActionResponsesTest {

    // ==================== InsightsResponse Tests ====================

    @Test
    void testInsightsResponse_Constructor() {
        // Act
        InsightsResponse response = new InsightsResponse();

        // Assert
        assertNotNull(response);
    }

    @Test
    void testInsightsResponse_SetAndGetResponse() {
        // Arrange
        InsightsResponse response = new InsightsResponse();
        String sentimentData = "[0.5, 0.7, 0.3, 0.8, 0.6]";

        // Act
        response.setResponse(sentimentData);
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals(sentimentData, result);
    }

    @Test
    void testInsightsResponse_SetNullResponse() {
        // Arrange
        InsightsResponse response = new InsightsResponse();

        // Act
        response.setResponse(null);
        String result = response.getResponse();

        // Assert
        assertNull(result);
    }

    @Test
    void testInsightsResponse_SetEmptyResponse() {
        // Arrange
        InsightsResponse response = new InsightsResponse();

        // Act
        response.setResponse("");
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testInsightsResponse_MultipleSetCalls() {
        // Arrange
        InsightsResponse response = new InsightsResponse();

        // Act
        response.setResponse("[0.1, 0.2]");
        response.setResponse("[0.3, 0.4, 0.5]");
        response.setResponse("[0.6, 0.7, 0.8, 0.9]");
        String result = response.getResponse();

        // Assert
        assertEquals("[0.6, 0.7, 0.8, 0.9]", result);
    }

    @Test
    void testInsightsResponse_JsonArrayFormat() {
        // Arrange
        InsightsResponse response = new InsightsResponse();
        String jsonArray = "[" +
                "{\"user\":\"Alice\",\"sentiment\":0.8}," +
                "{\"user\":\"Bob\",\"sentiment\":0.6}," +
                "{\"user\":\"Charlie\",\"sentiment\":0.7}" +
                "]";

        // Act
        response.setResponse(jsonArray);
        String result = response.getResponse();

        // Assert
        assertEquals(jsonArray, result);
    }

    @Test
    void testInsightsResponse_FloatValues() {
        // Arrange
        InsightsResponse response = new InsightsResponse();
        String floatData = "[0.123456, 0.789012, 0.345678]";

        // Act
        response.setResponse(floatData);
        String result = response.getResponse();

        // Assert
        assertEquals(floatData, result);
    }

    @Test
    void testInsightsResponse_NegativeValues() {
        // Arrange
        InsightsResponse response = new InsightsResponse();
        String negativeData = "[-0.5, -0.3, 0.2, 0.8]";

        // Act
        response.setResponse(negativeData);
        String result = response.getResponse();

        // Assert
        assertEquals(negativeData, result);
    }

    @Test
    void testInsightsResponse_LongArray() {
        // Arrange
        InsightsResponse response = new InsightsResponse();
        StringBuilder longArray = new StringBuilder("[");
        for (int i = 0; i < 100; i++) {
            if (i > 0) longArray.append(", ");
            longArray.append("0.").append(i);
        }
        longArray.append("]");

        // Act
        response.setResponse(longArray.toString());
        String result = response.getResponse();

        // Assert
        assertEquals(longArray.toString(), result);
    }

    // ==================== ActionItemsResponse Tests ====================

    @Test
    void testActionItemsResponse_Constructor() {
        // Act
        ActionItemsResponse response = new ActionItemsResponse();

        // Assert
        assertNotNull(response);
    }

    @Test
    void testActionItemsResponse_SetAndGetResponse() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();
        String actions = "[{\"action\":\"Review code\",\"assignee\":\"Alice\"}]";

        // Act
        response.setResponse(actions);
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals(actions, result);
    }

    @Test
    void testActionItemsResponse_SetNullResponse() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();

        // Act
        response.setResponse(null);
        String result = response.getResponse();

        // Assert
        assertNull(result);
    }

    @Test
    void testActionItemsResponse_SetEmptyResponse() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();

        // Act
        response.setResponse("");
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testActionItemsResponse_MultipleSetCalls() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();

        // Act
        response.setResponse("[{\"action\":\"Task 1\"}]");
        response.setResponse("[{\"action\":\"Task 2\"},{\"action\":\"Task 3\"}]");
        response.setResponse("[{\"action\":\"Final Task\"}]");
        String result = response.getResponse();

        // Assert
        assertEquals("[{\"action\":\"Final Task\"}]", result);
    }

    @Test
    void testActionItemsResponse_ComplexJsonStructure() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();
        String complexJson = "[" +
                "{\"action\":\"Complete code review\"," +
                "\"assignee\":\"Alice\"," +
                "\"dueDate\":\"2024-12-31\"," +
                "\"priority\":\"high\"," +
                "\"status\":\"pending\"}," +
                "{\"action\":\"Update documentation\"," +
                "\"assignee\":\"Bob\"," +
                "\"dueDate\":\"2024-12-25\"," +
                "\"priority\":\"medium\"," +
                "\"status\":\"in-progress\"}" +
                "]";

        // Act
        response.setResponse(complexJson);
        String result = response.getResponse();

        // Assert
        assertEquals(complexJson, result);
    }

    @Test
    void testActionItemsResponse_EmptyJsonArray() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();
        String emptyArray = "[]";

        // Act
        response.setResponse(emptyArray);
        String result = response.getResponse();

        // Assert
        assertEquals(emptyArray, result);
    }

    @Test
    void testActionItemsResponse_SingleAction() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();
        String singleAction = "[{\"action\":\"Deploy to production\"}]";

        // Act
        response.setResponse(singleAction);
        String result = response.getResponse();

        // Assert
        assertEquals(singleAction, result);
    }

    @Test
    void testActionItemsResponse_MultipleActions() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();
        StringBuilder multipleActions = new StringBuilder("[");
        for (int i = 1; i <= 10; i++) {
            if (i > 1) multipleActions.append(",");
            multipleActions.append("{\"action\":\"Task ").append(i).append("\"}");
        }
        multipleActions.append("]");

        // Act
        response.setResponse(multipleActions.toString());
        String result = response.getResponse();

        // Assert
        assertEquals(multipleActions.toString(), result);
    }

    @Test
    void testActionItemsResponse_WithSpecialCharacters() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();
        String specialChars = "[{\"action\":\"Fix bug #123 & update docs (urgent!)\"}]";

        // Act
        response.setResponse(specialChars);
        String result = response.getResponse();

        // Assert
        assertEquals(specialChars, result);
    }

    @Test
    void testActionItemsResponse_WithEscapedQuotes() {
        // Arrange
        ActionItemsResponse response = new ActionItemsResponse();
        String escapedQuotes = "[{\"action\":\"Update \\\"configuration\\\" file\"}]";

        // Act
        response.setResponse(escapedQuotes);
        String result = response.getResponse();

        // Assert
        assertEquals(escapedQuotes, result);
    }

    // ==================== Cross-Response Tests ====================

    @Test
    void testBothResponses_ImplementAiResponseInterface() {
        // Arrange & Act
        AiResponse insightsResponse = new InsightsResponse();
        AiResponse actionResponse = new ActionItemsResponse();

        // Assert
        assertNotNull(insightsResponse);
        assertNotNull(actionResponse);

        // Both should have setResponse and getResponse methods
        String testData = "Test data";

        insightsResponse.setResponse(testData);
        assertEquals(testData, insightsResponse.getResponse());

        actionResponse.setResponse(testData);
        assertEquals(testData, actionResponse.getResponse());
    }

    @Test
    void testBothResponses_GetResponseBeforeSet() {
        // Arrange & Act
        InsightsResponse insightsResponse = new InsightsResponse();
        ActionItemsResponse actionResponse = new ActionItemsResponse();

        // Assert - Should return null
        assertNull(insightsResponse.getResponse());
        assertNull(actionResponse.getResponse());
    }

    @Test
    void testBothResponses_SetMultipleTimes() {
        // Arrange
        AiResponse[] responses = {
                new InsightsResponse(),
                new ActionItemsResponse()
        };

        // Act & Assert
        for (AiResponse response : responses) {
            response.setResponse("First");
            assertEquals("First", response.getResponse());

            response.setResponse("Second");
            assertEquals("Second", response.getResponse());

            response.setResponse("Third");
            assertEquals("Third", response.getResponse());
        }
    }
}