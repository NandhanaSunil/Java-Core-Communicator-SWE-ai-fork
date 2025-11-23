package com.swe.aiinsights;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.aiinsights.request.AiRequestable;
import com.swe.aiinsights.request.RequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RequestFactory with 100% code coverage.
 */
class RequestFactoryTest {

    private RequestFactory requestFactory;
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        requestFactory = new RequestFactory();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetRequest_DESC_WithFilePath() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test-image.png");
        Files.write(testFile, "test image data".getBytes());
        String filePath = testFile.toString();

        // Act
        AiRequestable request = requestFactory.getRequest("DESC", filePath);

        // Assert
        assertNotNull(request);
        assertEquals("DESC", request.getReqType());
        assertNotNull(request.getContext());
        assertTrue(request.getContext().contains("Describe this image") ||
                request.getContext().contains("describe") ||
                request.getContext().contains("image"));
    }

    @Test
    void testGetRequest_DESC_WithInvalidFile() {
        // Arrange
        String invalidFilePath = "/nonexistent/file.png";

        // Act & Assert
        assertThrows(IOException.class, () -> {
            requestFactory.getRequest("DESC", invalidFilePath);
        });
    }

    @Test
    void testGetRequest_REG_WithValidPoints() throws IOException {
        // Arrange
        String points = "{\"points\": [{\"x\": 10, \"y\": 20}, {\"x\": 30, \"y\": 40}]}";

        // Act
        AiRequestable request = requestFactory.getRequest("REG", points);

        // Assert
        assertNotNull(request);
        assertEquals("REG", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testGetRequest_REG_WithEmptyPoints() throws IOException {
        // Arrange
        String points = "";

        // Act
        AiRequestable request = requestFactory.getRequest("REG", points);

        // Assert
        assertNotNull(request);
        assertEquals("REG", request.getReqType());
    }

    @Test
    void testGetRequest_INS_WithChatData() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree(
                "{\"messages\": [{\"user\": \"Alice\", \"text\": \"Hello\"}]}"
        );

        // Act
        AiRequestable request = requestFactory.getRequest("INS", chatData);

        // Assert
        assertNotNull(request);
        assertEquals("INS", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testGetRequest_INS_WithEmptyChatData() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");

        // Act
        AiRequestable request = requestFactory.getRequest("INS", chatData);

        // Assert
        assertNotNull(request);
        assertEquals("INS", request.getReqType());
    }

    @Test
    void testGetRequest_SUM_WithChatContent() throws IOException {
        // Arrange
        String contentToSummarise = "{\"chat\": \"Meeting discussion about project timeline\"}";

        // Act
        AiRequestable request = requestFactory.getRequest("SUM", contentToSummarise);

        // Assert
        assertNotNull(request);
        assertEquals("SUM", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testGetRequest_SUM_WithEmptyContent() throws IOException {
        // Arrange
        String contentToSummarise = "";

        // Act
        AiRequestable request = requestFactory.getRequest("SUM", contentToSummarise);

        // Assert
        assertNotNull(request);
        assertEquals("SUM", request.getReqType());
    }

    @Test
    void testGetRequest_ACTION_WithChatData() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree(
                "{\"messages\": [{\"user\": \"Bob\", \"text\": \"We need to complete the review\"}]}"
        );

        // Act
        AiRequestable request = requestFactory.getRequest("ACTION", chatData);

        // Assert
        assertNotNull(request);
        assertEquals("ACTION", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testGetRequest_ACTION_WithEmptyData() throws IOException {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{}");

        // Act
        AiRequestable request = requestFactory.getRequest("ACTION", chatData);

        // Assert
        assertNotNull(request);
        assertEquals("ACTION", request.getReqType());
    }

    @Test
    void testGetRequest_QNA_WithQuestionAndSummary() throws IOException {
        // Arrange
        String question = "What were the main points discussed?";
        String accumulatedSummary = "The team discussed project timeline and budget constraints.";

        // Act
        AiRequestable request = requestFactory.getRequest("QNA", question, accumulatedSummary);

        // Assert
        assertNotNull(request);
        assertEquals("QNA", request.getReqType());
        assertNotNull(request.getContext());
    }

    @Test
    void testGetRequest_QNA_WithNullSummary() throws IOException {
        // Arrange
        String question = "What is AI?";
        String accumulatedSummary = null;

        // Act
        AiRequestable request = requestFactory.getRequest("QNA", question, accumulatedSummary);

        // Assert
        assertNotNull(request);
        assertEquals("QNA", request.getReqType());
    }

    @Test
    void testGetRequest_QNA_WithEmptyQuestion() throws IOException {
        // Arrange
        String question = "";
        String accumulatedSummary = "Some summary";

        // Act
        AiRequestable request = requestFactory.getRequest("QNA", question, accumulatedSummary);

        // Assert
        assertNotNull(request);
        assertEquals("QNA", request.getReqType());
    }

    @Test
    void testGetRequest_DefaultCase_UnknownType() throws IOException {
        // Arrange
        String unknownType = "UNKNOWN";

        // Act
        AiRequestable request = requestFactory.getRequest(unknownType, "some data");

        // Assert
        assertNull(request);
    }

    @Test
    void testGetRequest_DefaultCase_NullType() {
        // Act & Assert - RequestFactory switch will throw NullPointerException
        assertThrows(NullPointerException.class, () -> {
            requestFactory.getRequest(null, "some data");
        });
    }

    @Test
    void testGetRequest_DefaultCase_EmptyType() throws IOException {
        // Act
        AiRequestable request = requestFactory.getRequest("", "some data");

        // Assert
        assertNull(request);
    }

    @Test
    void testGetRequest_AllTypes_Sequential() throws IOException {
        // Test all request types in sequence
        Path testFile = tempDir.resolve("test.png");
        Files.write(testFile, "test".getBytes());

        JsonNode jsonData = objectMapper.readTree("{}");

        // Act & Assert
        AiRequestable descReq = requestFactory.getRequest("DESC", testFile.toString());
        assertNotNull(descReq);
        assertEquals("DESC", descReq.getReqType());

        AiRequestable regReq = requestFactory.getRequest("REG", "{}");
        assertNotNull(regReq);
        assertEquals("REG", regReq.getReqType());

        AiRequestable insReq = requestFactory.getRequest("INS", jsonData);
        assertNotNull(insReq);
        assertEquals("INS", insReq.getReqType());

        AiRequestable sumReq = requestFactory.getRequest("SUM", "content");
        assertNotNull(sumReq);
        assertEquals("SUM", sumReq.getReqType());

        AiRequestable actionReq = requestFactory.getRequest("ACTION", jsonData);
        assertNotNull(actionReq);
        assertEquals("ACTION", actionReq.getReqType());

        AiRequestable qnaReq = requestFactory.getRequest("QNA", "question", "summary");
        assertNotNull(qnaReq);
        assertEquals("QNA", qnaReq.getReqType());
    }

    @Test
    void testGetRequest_CaseSensitivity() throws IOException {
        // Test that request types are case-sensitive
        // Create a valid temp file for testing
        Path testFile = tempDir.resolve("test.png");
        Files.write(testFile, "test".getBytes());

        // Only test with the valid file path for DESC
        AiRequestable upperCase = requestFactory.getRequest("DESC", testFile.toString());

        // For lowercase, should return null (default case in switch)
        AiRequestable lowerCase = requestFactory.getRequest("desc", "dummy");
        AiRequestable mixedCase = requestFactory.getRequest("Desc", "dummy");

        // Assert - Only exact case should work
        assertNotNull(upperCase);
        assertNull(lowerCase);
        assertNull(mixedCase);
    }

    @Test
    void testGetRequest_WithNullArgs() {
        // Act & Assert - Will throw exception when trying to cast null
        assertThrows(Exception.class, () -> {
            requestFactory.getRequest("DESC", (Object) null);
        });
    }

    @Test
    void testGetRequest_WithMultipleArgsWrongType() {
        // Act & Assert - QNA needs 2 arguments, providing only 1 should cause array index issue
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            requestFactory.getRequest("QNA", "only-one-arg");
        });
    }

    @Test
    void testGetRequest_DESC_WithValidImageFile() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("valid-image.jpg");
        byte[] imageData = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPEG header
        Files.write(imageFile, imageData);

        // Act
        AiRequestable request = requestFactory.getRequest("DESC", imageFile.toString());

        // Assert
        assertNotNull(request);
        assertEquals("DESC", request.getReqType());
    }

    @Test
    void testGetRequest_REG_WithComplexPoints() throws IOException {
        // Arrange
        String complexPoints = "{\"ShapeId\":\"shape1\",\"points\":[" +
                "{\"x\":10.5,\"y\":20.3},{\"x\":30.7,\"y\":40.9}," +
                "{\"x\":50.1,\"y\":60.2}],\"Color\":\"red\"}";

        // Act
        AiRequestable request = requestFactory.getRequest("REG", complexPoints);

        // Assert
        assertNotNull(request);
        assertEquals("REG", request.getReqType());
    }

    @Test
    void testGetRequest_SUM_WithPreviousSummary() throws IOException {
        // Arrange
        String contentWithPrevious = "Previous Summary: First meeting summary\n\n" +
                "New Chat Data: {\"chat\": \"Second meeting discussion\"}";

        // Act
        AiRequestable request = requestFactory.getRequest("SUM", contentWithPrevious);

        // Assert
        assertNotNull(request);
        assertEquals("SUM", request.getReqType());
    }

    @Test
    void testGetRequest_QNA_WithLongSummary() throws IOException {
        // Arrange
        StringBuilder longSummary = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longSummary.append("Summary part ").append(i).append(". ");
        }
        String question = "What were the key points?";

        // Act
        AiRequestable request = requestFactory.getRequest("QNA", question, longSummary.toString());

        // Assert
        assertNotNull(request);
        assertEquals("QNA", request.getReqType());
    }
}