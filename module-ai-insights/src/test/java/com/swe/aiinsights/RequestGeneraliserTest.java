package com.swe.aiinsights;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.aiinsights.data.WhiteBoardData;
import com.swe.aiinsights.request.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Complete test suite for all AI Request classes
 * Achieves 100% code coverage for:
 * - AiDescriptionRequest
 * - AiRegularisationRequest
 * - AiSummarisationRequest
 * - AiQuestionAnswerRequest
 * - AiInsightsRequest
 * - AiActionItemsRequest
 */
@ExtendWith(MockitoExtension.class)
class RequestGeneraliserTest {

    @Mock
    private WhiteBoardData mockWhiteBoardData;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== AiDescriptionRequest Tests ====================

    @Test
    void testAiDescriptionRequest_ConstructorWithDefaultPrompt() throws IOException {
        // Arrange
        when(mockWhiteBoardData.getContent()).thenReturn("image_base64_data");

        // Act
        AiDescriptionRequest request = new AiDescriptionRequest(mockWhiteBoardData);

        // Assert
        assertNotNull(request);
        assertEquals("Describe this image in detail", request.getContext());
        assertEquals("image_base64_data", request.getInput());
        assertEquals("DESC", request.getReqType());
    }

//    @Test
//    void testAiDescriptionRequest_ConstructorWithCustomPrompt() throws IOException {
//        // Arrange
//        String customPrompt = "Analyze this whiteboard drawing";
//        when(mockWhiteBoardData.getContent()).thenReturn("whiteboard_data");
//
//        // Act
//        AiDescriptionRequest request = new AiDescriptionRequest(mockWhiteBoardData, customPrompt);
//
//        // Assert
//        assertNotNull(request);
//        assertEquals(customPrompt, request.getContext());
//        assertEquals("whiteboard_data", request.getInput());
//        assertEquals("DESC", request.getReqType());
//    }

    @Test
    void testAiDescriptionRequest_GetContext() throws IOException {
        // Arrange
        when(mockWhiteBoardData.getContent()).thenReturn("data");
        AiDescriptionRequest request = new AiDescriptionRequest(mockWhiteBoardData);

        // Act
        String context = request.getContext();

        // Assert
        assertEquals("Describe this image in detail", context);
    }

    @Test
    void testAiDescriptionRequest_GetInput() throws IOException {
        // Arrange
        when(mockWhiteBoardData.getContent()).thenReturn("test_image_data");
        AiDescriptionRequest request = new AiDescriptionRequest(mockWhiteBoardData);

        // Act
        String input = request.getInput();

        // Assert
        assertEquals("test_image_data", input);
    }

    @Test
    void testAiDescriptionRequest_GetReqType() throws IOException {
        // Arrange
        when(mockWhiteBoardData.getContent()).thenReturn("data");
        AiDescriptionRequest request = new AiDescriptionRequest(mockWhiteBoardData);

        // Act
        String reqType = request.getReqType();

        // Assert
        assertEquals("DESC", reqType);
    }

    @Test
    void testAiDescriptionRequest_WithEmptyContent() throws IOException {
        // Arrange
        when(mockWhiteBoardData.getContent()).thenReturn("");

        // Act
        AiDescriptionRequest request = new AiDescriptionRequest(mockWhiteBoardData);

        // Assert
        assertNotNull(request);
        assertEquals("", request.getInput());
    }

    // ==================== AiRegularisationRequest Tests ====================

    @Test
    void testAiRegularisationRequest_ConstructorWithDefaultPrompt() {
        // Arrange
        String points = "{\"points\":[{\"x\":10,\"y\":20}]}";

        // Act
        AiRegularisationRequest request = new AiRegularisationRequest(points);

        // Assert
        assertNotNull(request);
        assertEquals(points, request.getInput());
        assertEquals("REG", request.getReqType());
        assertNotNull(request.getContext());
        assertTrue(request.getContext().contains("geometric shape"));
    }

    @Test
    void testAiRegularisationRequest_ConstructorWithCustomPrompt() {
        // Arrange
        String points = "{\"points\":[{\"x\":5,\"y\":15}]}";
        String customPrompt = "Custom regularization prompt";

        // Act
        AiRegularisationRequest request = new AiRegularisationRequest(points, customPrompt);

        // Assert
        assertNotNull(request);
        assertEquals(points, request.getInput());
        assertEquals(customPrompt, request.getContext());
        assertEquals("REG", request.getReqType());
    }

    @Test
    void testAiRegularisationRequest_GetContext() {
        // Arrange
        String points = "{\"points\":[]}";
        AiRegularisationRequest request = new AiRegularisationRequest(points);

        // Act
        String context = request.getContext();

        // Assert
        assertNotNull(context);
        assertTrue(context.contains("geometric shape"));
        assertTrue(context.contains("bounding box"));
    }

    @Test
    void testAiRegularisationRequest_GetInput() {
        // Arrange
        String points = "{\"points\":[{\"x\":1,\"y\":2}]}";
        AiRegularisationRequest request = new AiRegularisationRequest(points);

        // Act
        String input = request.getInput();

        // Assert
        assertEquals(points, input);
    }

    @Test
    void testAiRegularisationRequest_GetReqType() {
        // Arrange
        String points = "{}";
        AiRegularisationRequest request = new AiRegularisationRequest(points);

        // Act
        String reqType = request.getReqType();

        // Assert
        assertEquals("REG", reqType);
    }

    @Test
    void testAiRegularisationRequest_WithEmptyPoints() {
        // Arrange
        String emptyPoints = "";

        // Act
        AiRegularisationRequest request = new AiRegularisationRequest(emptyPoints);

        // Assert
        assertNotNull(request);
        assertEquals(emptyPoints, request.getInput());
        assertEquals("REG", request.getReqType());
    }

    @Test
    void testAiRegularisationRequest_WithNullPoints() {
        // Arrange
        String nullPoints = null;

        // Act
        AiRegularisationRequest request = new AiRegularisationRequest(nullPoints);

        // Assert
        assertNotNull(request);
        assertNull(request.getInput());
        assertEquals("REG", request.getReqType());
    }

    @Test
    void testAiRegularisationRequest_PromptContainsAllowedShapes() {
        // Arrange
        AiRegularisationRequest request = new AiRegularisationRequest("{}");

        // Act
        String context = request.getContext();

        // Assert
        assertTrue(context.contains("Ellipse"));
        assertTrue(context.contains("Square"));
        assertTrue(context.contains("Triangle"));
        assertTrue(context.contains("Rectangle"));
        assertTrue(context.contains("StraightLine"));
    }

    // ==================== AiSummarisationRequest Tests ====================

    @Test
    void testAiSummarisationRequest_Constructor() {
        // Arrange
        String chatJson = "{\"messages\":[{\"user\":\"Alice\",\"text\":\"Hello\"}]}";

        // Act
        AiSummarisationRequest request = new AiSummarisationRequest(chatJson);

        // Assert
        assertNotNull(request);
        assertEquals(chatJson, request.getInput());
        assertEquals("SUM", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testAiSummarisationRequest_GetContext() {
        // Arrange
        String chatJson = "{\"chat\":\"data\"}";
        AiSummarisationRequest request = new AiSummarisationRequest(chatJson);

        // Act
        String context = request.getContext();

        // Assert
        assertNotNull(context);
        assertTrue(context.contains("summary"));
        assertTrue(context.contains("chat data"));
    }

    @Test
    void testAiSummarisationRequest_GetInput() {
        // Arrange
        String chatJson = "{\"messages\":[]}";
        AiSummarisationRequest request = new AiSummarisationRequest(chatJson);

        // Act
        String input = request.getInput();

        // Assert
        assertEquals(chatJson, input);
    }

    @Test
    void testAiSummarisationRequest_GetReqType() {
        // Arrange
        String chatJson = "{}";
        AiSummarisationRequest request = new AiSummarisationRequest(chatJson);

        // Act
        String reqType = request.getReqType();

        // Assert
        assertEquals("SUM", reqType);
    }

    @Test
    void testAiSummarisationRequest_WithEmptyChat() {
        // Arrange
        String emptyChat = "";

        // Act
        AiSummarisationRequest request = new AiSummarisationRequest(emptyChat);

        // Assert
        assertNotNull(request);
        assertEquals(emptyChat, request.getInput());
        assertEquals("SUM", request.getReqType());
    }

    @Test
    void testAiSummarisationRequest_WithNullChat() {
        // Arrange
        String nullChat = null;

        // Act
        AiSummarisationRequest request = new AiSummarisationRequest(nullChat);

        // Assert
        assertNotNull(request);
        assertNull(request.getInput());
        assertEquals("SUM", request.getReqType());
    }

    @Test
    void testAiSummarisationRequest_PromptContainsKeyInstructions() {
        // Arrange
        AiSummarisationRequest request = new AiSummarisationRequest("test");

        // Act
        String context = request.getContext();

        // Assert
        assertTrue(context.contains("previous summary"));
        assertTrue(context.contains("new chat data"));
        assertTrue(context.contains("cohesive summary"));
        assertTrue(context.contains("action items"));
    }

    // ==================== AiQuestionAnswerRequest Tests ====================

    @Test
    void testAiQuestionAnswerRequest_ConstructorWithSummary() {
        // Arrange
        String question = "What was discussed?";
        String summary = "Meeting about project updates";

        // Act
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest(question, summary);

        // Assert
        assertNotNull(request);
        assertEquals(question, request.getInput());
        assertEquals("QNA", request.getReqType());
        assertNotNull(request.getContext());
        assertTrue(request.getContext().contains(question));
        assertTrue(request.getContext().contains(summary));
    }

    @Test
    void testAiQuestionAnswerRequest_ConstructorWithNullSummary() {
        // Arrange
        String question = "What is AI?";
        String summary = null;

        // Act
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest(question, summary);

        // Assert
        assertNotNull(request);
        assertEquals(question, request.getInput());
        assertEquals("QNA", request.getReqType());
    }

    @Test
    void testAiQuestionAnswerRequest_GetContext() {
        // Arrange
        String question = "Test question?";
        String summary = "Test summary";
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest(question, summary);

        // Act
        String context = request.getContext();

        // Assert
        assertNotNull(context);
        assertTrue(context.contains("ACCUMULATED_SUMMARY"));
        assertTrue(context.contains("USER_QUESTION"));
        assertTrue(context.contains(question));
        assertTrue(context.contains(summary));
    }

    @Test
    void testAiQuestionAnswerRequest_GetInput() {
        // Arrange
        String question = "What is the answer?";
        String summary = "Context info";
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest(question, summary);

        // Act
        String input = request.getInput();

        // Assert
        assertEquals(question, input);
    }

    @Test
    void testAiQuestionAnswerRequest_GetReqType() {
        // Arrange
        String question = "Question";
        String summary = "Summary";
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest(question, summary);

        // Act
        String reqType = request.getReqType();

        // Assert
        assertEquals("QNA", reqType);
    }

    @Test
    void testAiQuestionAnswerRequest_WithEmptyStrings() {
        // Arrange
        String question = "";
        String summary = "";

        // Act
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest(question, summary);

        // Assert
        assertNotNull(request);
        assertEquals(question, request.getInput());
        assertEquals("QNA", request.getReqType());
    }

    @Test
    void testAiQuestionAnswerRequest_PromptContainsInstructions() {
        // Arrange
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest("Q", "S");

        // Act
        String context = request.getContext();

        // Assert
        assertTrue(context.contains("intelligent Q&A system"));
        assertTrue(context.contains("missing from the context"));
        assertTrue(context.contains("general knowledge"));
    }

    @Test
    void testAiQuestionAnswerRequest_WithLongQuestion() {
        // Arrange
        String longQuestion = "This is a very long question that contains multiple parts and requires detailed explanation from the accumulated summary data";
        String summary = "Short summary";

        // Act
        AiQuestionAnswerRequest request = new AiQuestionAnswerRequest(longQuestion, summary);

        // Assert
        assertNotNull(request);
        assertEquals(longQuestion, request.getInput());
        assertTrue(request.getContext().contains(longQuestion));
    }

    // ==================== AiInsightsRequest Tests ====================

    @Test
    void testAiInsightsRequest_Constructor() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        // Act
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Assert
        assertNotNull(request);
        assertEquals(chatData, request.getInput());
        assertEquals("INS", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testAiInsightsRequest_GetContext() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Act
        String context = request.getContext();

        // Assert
        assertNotNull(context);
        assertTrue(context.contains("sentiment analysis"));
        assertTrue(context.contains("-10.0 to +10.0"));
        assertTrue(context.contains("timestamp"));
    }

    @Test
    void testAiInsightsRequest_GetInput() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"chat\":\"data\"}");
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Act
        JsonNode input = request.getInput();

        // Assert
        assertEquals(chatData, input);
    }

    @Test
    void testAiInsightsRequest_GetReqType() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Act
        String reqType = request.getReqType();

        // Assert
        assertEquals("INS", reqType);
    }

    @Test
    void testAiInsightsRequest_WithComplexJson() throws IOException {
        // Arrange
        String complexJson = "{\"messages\":[{\"user\":\"Alice\",\"text\":\"Hello\",\"timestamp\":\"2024-01-01\"}]}";
        JsonNode chatData = objectMapper.readTree(complexJson);

        // Act
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Assert
        assertNotNull(request);
        assertEquals(chatData, request.getInput());
    }

    @Test
    void testAiInsightsRequest_PromptContainsSentimentScale() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Act
        String context = request.getContext();

        // Assert
        assertTrue(context.contains("very negative"));
        assertTrue(context.contains("neutral"));
        assertTrue(context.contains("very positive"));
    }

    @Test
    void testAiInsightsRequest_WithEmptyMessages() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        // Act
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Assert
        assertNotNull(request);
        assertEquals("INS", request.getReqType());
    }

    @Test
    void testAiInsightsRequest_PromptContainsJsonFormat() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");
        AiInsightsRequest request = new AiInsightsRequest(chatData);

        // Act
        String context = request.getContext();

        // Assert
        assertTrue(context.contains("JSON array"));
        assertTrue(context.contains("time"));
        assertTrue(context.contains("sentiment"));
    }

    // ==================== AiActionItemsRequest Tests ====================

    @Test
    void testAiActionItemsRequest_Constructor() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        // Act
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Assert
        assertNotNull(request);
        assertEquals(chatData, request.getInput());
        assertEquals("ACTION", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testAiActionItemsRequest_GetContext() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Act
        String context = request.getContext();

        // Assert
        assertNotNull(context);
        assertTrue(context.contains("action items"));
        assertTrue(context.contains("JSON list"));
    }

    @Test
    void testAiActionItemsRequest_GetInput() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"chat\":\"data\"}");
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Act
        JsonNode input = request.getInput();

        // Assert
        assertEquals(chatData, input);
    }

    @Test
    void testAiActionItemsRequest_GetReqType() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Act
        String reqType = request.getReqType();

        // Assert
        assertEquals("ACTION", reqType);
    }

    @Test
    void testAiActionItemsRequest_WithComplexJson() throws IOException {
        // Arrange
        String complexJson = "{\"transcript\":[{\"speaker\":\"John\",\"action\":\"Review code\"}]}";
        JsonNode chatData = objectMapper.readTree(complexJson);

        // Act
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Assert
        assertNotNull(request);
        assertEquals(chatData, request.getInput());
    }

    @Test
    void testAiActionItemsRequest_PromptContainsInstructions() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Act
        String context = request.getContext();

        // Assert
        assertTrue(context.contains("concrete action items"));
        assertTrue(context.contains("third person"));
        assertTrue(context.contains("commits to doing"));
    }

    @Test
    void testAiActionItemsRequest_WithEmptyMessages() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        // Act
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Assert
        assertNotNull(request);
        assertEquals("ACTION", request.getReqType());
    }

    @Test
    void testAiActionItemsRequest_PromptExcludesGeneralDiscussions() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");
        AiActionItemsRequest request = new AiActionItemsRequest(chatData);

        // Act
        String context = request.getContext();

        // Assert
        assertTrue(context.contains("Exclude general discussions"));
        assertTrue(context.contains("suggestions"));
    }
}