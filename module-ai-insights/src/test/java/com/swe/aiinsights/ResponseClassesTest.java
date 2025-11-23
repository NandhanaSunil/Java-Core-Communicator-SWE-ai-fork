package com.swe.aiinsights;

import com.swe.aiinsights.response.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for all Response classes.
 */
class ResponseClassesTest {

    @Test
    void testQuestionAnswerResponse() {
        QuestionAnswerResponse response = new QuestionAnswerResponse();
        String testResponse = "This is the answer to your question.";

        response.setResponse(testResponse);
        String result = response.getResponse();

        assertNotNull(result);
        assertEquals(testResponse, result);
    }

    @Test
    void testQuestionAnswerResponseSetNull() {
        QuestionAnswerResponse response = new QuestionAnswerResponse();
        response.setResponse(null);
        String result = response.getResponse();
        assertNull(result);
    }


    @Test
    void testRegulariserResponse() {
        RegulariserResponse response = new RegulariserResponse();
        assertNotNull(response);
    }

    @Test
    void testRegulariserResponseWithPoints() {
        RegulariserResponse response = new RegulariserResponse();
        String testContent = "{\n"
                + "  \"ShapeId\": \"c585b84a-d56c-45b8-a0e1-827ae20a014a\",\n"
                + "  \"Type\": \"FreeHand\",\n"
                + "  \"Points\": [\n"
                + "    {\n"
                + "      \"X\": 450,\n"
                + "      \"Y\": 60\n"
                + "    }\n"
                + "  ],\n"
                + "  \"Color\": \"#FF000000\",\n"
                + "  \"Thickness\": 2,\n"
                + "  \"CreatedBy\": \"user_default\",\n"
                + "  \"LastModifiedBy\": \"user_default\",\n"
                + "  \"IsDeleted\": false\n"
                + "}";
        response.setResponse(testContent);
        String result = response.getResponse();
        assertNotNull(result);
        assertEquals(testContent, result);
    }

    @Test
    void testRegulariserResponseNull() {
        RegulariserResponse response = new RegulariserResponse();
        response.setResponse(null);
        String result = response.getResponse();
        assertNull(result);
    }

    @Test
    void testRegulariserResponse_SetEmptyResponse() {
        // Arrange
        RegulariserResponse response = new RegulariserResponse();

        // Act
        response.setResponse("");
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testRegulariserResponse_MultipleSetCalls() {
        // Arrange
        RegulariserResponse response = new RegulariserResponse();

        // Act
        response.setResponse("{\"type\": \"Circle\"}");
        response.setResponse("{\"type\": \"Square\"}");
        response.setResponse("{\"type\": \"Triangle\"}");
        String result = response.getResponse();

        // Assert
        assertEquals("{\"type\": \"Triangle\"}", result);
    }

    @Test
    void testRegulariserResponse_ComplexJsonResponse() {
        // Arrange
        RegulariserResponse response = new RegulariserResponse();
        String complexJson = "{\"ShapeId\":\"shape123\",\"type\":\"Ellipse\"," +
                "\"Points\":[{\"x\":10.5,\"y\":20.3},{\"x\":50.7,\"y\":60.9}]," +
                "\"Color\":\"blue\",\"Thickness\":\"2\",\"CreatedBy\":\"user1\"," +
                "\"LastModifiedBy\":\"user2\",\"IsDeleted\":false}";

        // Act
        response.setResponse(complexJson);
        String result = response.getResponse();

        // Assert
        assertEquals(complexJson, result);
    }

    // ==================== SummariserResponse Tests ====================

    @Test
    void testSummariserResponse_SetAndGetResponse() {
        // Arrange
        SummariserResponse response = new SummariserResponse();
        String summary = "This is a comprehensive summary of the discussion.";

        // Act
        response.setResponse(summary);
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals(summary, result);
    }

    @Test
    void testSummariserResponse_GetResponseText() {
        // Arrange
        SummariserResponse response = new SummariserResponse();
        String summary = "Meeting summary with action items.";

        // Act
        response.setResponse(summary);
        String resultFromGetResponse = response.getResponse();
        String resultFromGetResponseText = response.getResponseText();

        // Assert
        assertEquals(summary, resultFromGetResponse);
        assertEquals(summary, resultFromGetResponseText);
        assertEquals(resultFromGetResponse, resultFromGetResponseText);
    }

    @Test
    void testSummariserResponse_SetNullResponse() {
        // Arrange
        SummariserResponse response = new SummariserResponse();

        // Act
        response.setResponse(null);
        String result = response.getResponse();
        String resultText = response.getResponseText();

        // Assert
        assertNull(result);
        assertNull(resultText);
    }

    @Test
    void testSummariserResponse_SetEmptyResponse() {
        // Arrange
        SummariserResponse response = new SummariserResponse();

        // Act
        response.setResponse("");
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testSummariserResponse_MultipleSetCalls() {
        // Arrange
        SummariserResponse response = new SummariserResponse();

        // Act
        response.setResponse("Initial summary");
        response.setResponse("Updated summary with more details");
        response.setResponse("Final comprehensive summary");
        String result = response.getResponse();

        // Assert
        assertEquals("Final comprehensive summary", result);
    }

    @Test
    void testSummariserResponse_LongSummary() {
        // Arrange
        SummariserResponse response = new SummariserResponse();
        StringBuilder longSummary = new StringBuilder();
        longSummary.append("Meeting Summary:\n");
        for (int i = 0; i < 100; i++) {
            longSummary.append("Point ").append(i + 1).append(": Important discussion topic. ");
        }

        // Act
        response.setResponse(longSummary.toString());
        String result = response.getResponse();
        String resultText = response.getResponseText();

        // Assert
        assertEquals(longSummary.toString(), result);
        assertEquals(longSummary.toString(), resultText);
    }

    @Test
    void testSummariserResponse_WithSpecialCharacters() {
        // Arrange
        SummariserResponse response = new SummariserResponse();
        String summaryWithSpecialChars = "Summary with special chars: @#$%^&*()_+{}[]|\\:\";<>?,./~`";

        // Act
        response.setResponse(summaryWithSpecialChars);
        String result = response.getResponse();

        // Assert
        assertEquals(summaryWithSpecialChars, result);
    }

    // ==================== InterpreterResponse Tests ====================

    @Test
    void testInterpreterResponse_Constructor() {
        // Act
        InterpreterResponse response = new InterpreterResponse();

        // Assert
        assertNotNull(response);
    }

    @Test
    void testInterpreterResponse_SetAndGetResponse() {
        // Arrange
        InterpreterResponse response = new InterpreterResponse();
        String description = "The image shows a whiteboard with mathematical equations.";

        // Act
        response.setResponse(description);
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals(description, result);
    }

    @Test
    void testInterpreterResponse_SetNullResponse() {
        // Arrange
        InterpreterResponse response = new InterpreterResponse();

        // Act
        response.setResponse(null);
        String result = response.getResponse();

        // Assert
        assertNull(result);
    }

    @Test
    void testInterpreterResponse_SetEmptyResponse() {
        // Arrange
        InterpreterResponse response = new InterpreterResponse();

        // Act
        response.setResponse("");
        String result = response.getResponse();

        // Assert
        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testInterpreterResponse_MultipleSetCalls() {
        // Arrange
        InterpreterResponse response = new InterpreterResponse();

        // Act
        response.setResponse("Initial description");
        response.setResponse("Refined description");
        response.setResponse("Final detailed description");
        String result = response.getResponse();

        // Assert
        assertEquals("Final detailed description", result);
    }

    @Test
    void testInterpreterResponse_DetailedImageDescription() {
        // Arrange
        InterpreterResponse response = new InterpreterResponse();
        String detailedDescription = "The image contains a complex diagram with multiple " +
                "geometric shapes including circles, rectangles, and triangles. " +
                "There are annotations with mathematical formulas and arrows " +
                "connecting different elements. The color scheme uses blue for " +
                "primary shapes and red for annotations.";

        // Act
        response.setResponse(detailedDescription);
        String result = response.getResponse();

        // Assert
        assertEquals(detailedDescription, result);
    }

    @Test
    void testInterpreterResponse_WithUnicodeCharacters() {
        // Arrange
        InterpreterResponse response = new InterpreterResponse();
        String descriptionWithUnicode = "Image contains text in multiple languages: " +
                "Hello, こんにちは, 你好, مرحبا, Здравствуйте";

        // Act
        response.setResponse(descriptionWithUnicode);
        String result = response.getResponse();

        // Assert
        assertEquals(descriptionWithUnicode, result);
    }

    // ==================== Cross-Response Tests ====================

    @Test
    void testAllResponses_ImplementAiResponseInterface() {
        // Arrange & Act
        AiResponse qnaResponse = new QuestionAnswerResponse();
        AiResponse regResponse = new RegulariserResponse();
        AiResponse sumResponse = new SummariserResponse();
        AiResponse intResponse = new InterpreterResponse();

        // Assert
        assertNotNull(qnaResponse);
        assertNotNull(regResponse);
        assertNotNull(sumResponse);
        assertNotNull(intResponse);

        // All should have setResponse and getResponse methods
        String testData = "Test data";

        qnaResponse.setResponse(testData);
        assertEquals(testData, qnaResponse.getResponse());

        regResponse.setResponse(testData);
        assertEquals(testData, regResponse.getResponse());

        sumResponse.setResponse(testData);
        assertEquals(testData, sumResponse.getResponse());

        intResponse.setResponse(testData);
        assertEquals(testData, intResponse.getResponse());
    }

    @Test
    void testAllResponses_GetResponseBeforeSet() {
        // Arrange & Act
        QuestionAnswerResponse qnaResponse = new QuestionAnswerResponse();
        RegulariserResponse regResponse = new RegulariserResponse();
        SummariserResponse sumResponse = new SummariserResponse();
        InterpreterResponse intResponse = new InterpreterResponse();

        // Assert - Should return null or handle gracefully
        assertNull(qnaResponse.getResponse());
        assertNull(regResponse.getResponse());
        assertNull(sumResponse.getResponse());
        assertNull(intResponse.getResponse());
    }

    @Test
    void testAllResponses_SetMultipleTimes() {
        // Arrange
        AiResponse[] responses = {
                new QuestionAnswerResponse(),
                new RegulariserResponse(),
                new SummariserResponse(),
                new InterpreterResponse()
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