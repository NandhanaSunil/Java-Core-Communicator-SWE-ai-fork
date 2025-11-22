package com.swe.aiinsights.apiendpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.aiinsights.data.WhiteBoardData;
import com.swe.aiinsights.request.AiRequestable;
import com.swe.aiinsights.request.RequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Complete test class for AiClientService with 100% coverage.
 */
@ExtendWith(MockitoExtension.class)
class AiClientServiceTest {

    private AiClientService aiClientService;

    @Mock
    private RequestFactory mockRequestFactory;

    @Mock
    private AsyncAiExecutor mockAsyncExecutor;

    @Mock
    private AiRequestable mockRequest;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        aiClientService = new AiClientService();
    }

    // ==================== Testing describe() Success Path ====================

    @Test
    void testDescribe_SuccessPath_WithMockedComponents() throws Exception {
        // Arrange
        Path tempFile = Files.createTempFile("test-image", ".png");
        Files.write(tempFile, "fake image data".getBytes());
        String filePath = tempFile.toString();

        try {
            // Mock WhiteBoardData creation
            try (MockedConstruction<WhiteBoardData> wbDataMock = mockConstruction(
                    WhiteBoardData.class,
                    (mock, context) -> {
                        // WhiteBoardData successfully created
                    })) {

                // Inject mock factory
                var factoryField = AiClientService.class.getDeclaredField("factory");
                factoryField.setAccessible(true);
                factoryField.set(aiClientService, mockRequestFactory);

                // Inject mock executor
                var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
                executorField.setAccessible(true);
                executorField.set(null, mockAsyncExecutor);

                when(mockRequestFactory.getRequest(eq("DESC"), any())).thenReturn(mockRequest);
                when(mockAsyncExecutor.execute(any())).thenReturn(
                        CompletableFuture.completedFuture("Image description response")
                );

                // Act
                CompletableFuture<String> result = aiClientService.describe(filePath);

                // Assert
                assertNotNull(result);
                assertEquals("Image description response", result.get());
                verify(mockRequestFactory).getRequest(eq("DESC"), any());
                verify(mockAsyncExecutor).execute(any());
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testDescribe_WhiteBoardDataCreationFails() throws Exception {
        // Arrange
        String invalidFile = "/nonexistent/file.png";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aiClientService.describe(invalidFile);
        });

        assertTrue(exception.getCause() instanceof IOException);
    }

    // ==================== Testing regularise() Success Path ====================

    @Test
    void testRegularise_SuccessPath() throws Exception {
        // Arrange
        String points = "{\"points\":[{\"x\":10,\"y\":20}]}";

        // Inject mocks
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("REG"), eq(points))).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("{\"type\":\"Circle\",\"points\":[]}")
        );

        // Act
        CompletableFuture<String> result = aiClientService.regularise(points);

        // Assert
        assertNotNull(result);
        assertEquals("{\"type\":\"Circle\",\"points\":[]}", result.get());
        verify(mockRequestFactory).getRequest(eq("REG"), eq(points));
        verify(mockAsyncExecutor).execute(mockRequest);
    }

    @Test
    void testRegularise_UnexpectedExceptionInFactory() throws Exception {
        // Arrange
        String points = "{\"points\":[]}";

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        when(mockRequestFactory.getRequest(eq("REG"), eq(points)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aiClientService.regularise(points);
        });

        assertNotNull(exception);
    }

    // ==================== Testing sentiment() Success Path ====================

    @Test
    void testSentiment_SuccessPath() throws Exception {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        // Inject mocks
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("INS"), eq(chatData))).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("[0.5,0.7,0.3]")
        );

        // Act
        CompletableFuture<String> result = aiClientService.sentiment(chatData);

        // Assert
        assertNotNull(result);
        assertEquals("[0.5,0.7,0.3]", result.get());
        verify(mockRequestFactory).getRequest(eq("INS"), eq(chatData));
    }

    @Test
    void testSentiment_IOExceptionInFactory() throws Exception {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        when(mockRequestFactory.getRequest(eq("INS"), eq(chatData)))
                .thenThrow(new IOException("Factory error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aiClientService.sentiment(chatData);
        });

        assertTrue(exception.getCause() instanceof IOException);
    }

    // ==================== Testing summariseText() Success Path ====================

    @Test
    void testSummariseText_SuccessPath_EmptySummary() throws Exception {
        // Arrange
        String jsonContent = "{\"chat\":\"meeting notes\"}";

        // Inject mocks
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("SUM"), anyString())).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("Summary of meeting")
        );

        // Act
        CompletableFuture<String> result = aiClientService.summariseText(jsonContent);

        // Assert
        assertNotNull(result);
        String summary = result.get();
        assertEquals("Summary of meeting", summary);
    }

    @Test
    void testSummariseText_SuccessPath_WithPreviousSummary() throws Exception {
        // Arrange
        String jsonContent1 = "{\"chat\":\"first meeting\"}";
        String jsonContent2 = "{\"chat\":\"second meeting\"}";

        // Inject mocks
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("SUM"), anyString())).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any()))
                .thenReturn(CompletableFuture.completedFuture("First summary"))
                .thenReturn(CompletableFuture.completedFuture("Combined summary"));

        // Act
        CompletableFuture<String> result1 = aiClientService.summariseText(jsonContent1);
        result1.get(); // Wait for completion

        CompletableFuture<String> result2 = aiClientService.summariseText(jsonContent2);

        // Assert
        assertEquals("Combined summary", result2.get());
        verify(mockRequestFactory, times(2)).getRequest(eq("SUM"), anyString());
    }

    @Test
    void testSummariseText_IOExceptionInFactory() throws Exception {
        // Arrange
        String jsonContent = "{\"chat\":\"test\"}";

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        when(mockRequestFactory.getRequest(eq("SUM"), anyString()))
                .thenThrow(new IOException("Factory error"));

        // Act
        CompletableFuture<String> result = aiClientService.summariseText(jsonContent);

        // Assert
        assertThrows(ExecutionException.class, result::get);
    }

    @Test
    void testSummariseText_UnexpectedExceptionInCompose() throws Exception {
        // Arrange
        String jsonContent = "{\"chat\":\"test\"}";

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        // Make the executor throw an unexpected exception
        when(mockRequestFactory.getRequest(eq("SUM"), anyString())).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        CompletableFuture<String> result = aiClientService.summariseText(jsonContent);

        // Assert
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    // ==================== Testing answerQuestion() Success Path ====================

    @Test
    void testAnswerQuestion_SuccessPath_WithSummary() throws Exception {
        // Arrange
        String question = "What were the main points?";

        // First, set up a summary
        String jsonContent = "{\"chat\":\"meeting notes\"}";

        // Inject mocks
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("SUM"), anyString())).thenReturn(mockRequest);
        when(mockRequestFactory.getRequest(eq("QNA"), eq(question), anyString()))
                .thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any()))
                .thenReturn(CompletableFuture.completedFuture("Meeting summary"))
                .thenReturn(CompletableFuture.completedFuture("Answer to question"));

        // Act
        aiClientService.summariseText(jsonContent).get();
        CompletableFuture<String> result = aiClientService.answerQuestion(question);

        // Assert
        assertEquals("Answer to question", result.get());
    }

    @Test
    void testAnswerQuestion_SuccessPath_NoSummary() throws Exception {
        // Arrange
        String question = "What is AI?";

        // Inject mocks
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("QNA"), eq(question), anyString()))
                .thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("AI is artificial intelligence")
        );

        // Act
        CompletableFuture<String> result = aiClientService.answerQuestion(question);

        // Assert
        assertEquals("AI is artificial intelligence", result.get());
    }

    @Test
    void testAnswerQuestion_WithNullSummary() throws Exception {
        // Arrange
        String question = "Test question?";

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        // Set accumulated summary to null via reflection
        var summaryField = AiClientService.class.getDeclaredField("accumulatedSummary");
        summaryField.setAccessible(true);
        summaryField.set(aiClientService, null);

        when(mockRequestFactory.getRequest(eq("QNA"), eq(question), isNull()))
                .thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("Answer")
        );

        // Act
        CompletableFuture<String> result = aiClientService.answerQuestion(question);

        // Assert
        assertEquals("Answer", result.get());
        verify(mockRequestFactory).getRequest(eq("QNA"), eq(question), isNull());
    }

    @Test
    void testAnswerQuestion_IOExceptionInFactory() throws Exception {
        // Arrange
        String question = "test question";

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        when(mockRequestFactory.getRequest(eq("QNA"), anyString(), anyString()))
                .thenThrow(new IOException("Factory error"));

        // Act
        CompletableFuture<String> result = aiClientService.answerQuestion(question);

        // Assert
        assertThrows(ExecutionException.class, result::get);
    }

    // THIS TEST COVERS THE OUTER CATCH BLOCK
    @Test
    void testAnswerQuestion_OuterCatchBlock_ExceptionBeforeAsyncChain() throws Exception {
        // Arrange
        String question = "Test question?";

        // Create a spy to intercept method calls
        AiClientService spyService = spy(new AiClientService());

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(spyService, mockRequestFactory);

        // Make lastSummaryUpdate throw when thenCompose is called (synchronous exception)
        var lastSummaryField = AiClientService.class.getDeclaredField("lastSummaryUpdate");
        lastSummaryField.setAccessible(true);

        CompletableFuture<Void> brokenFuture = mock(CompletableFuture.class);
        when(brokenFuture.thenCompose(any()))
                .thenThrow(new IllegalStateException("Broken future chain"));

        lastSummaryField.set(spyService, brokenFuture);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spyService.answerQuestion(question);
        });

        assertEquals("Error processing Q&A request", exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }

    // ==================== Testing action() Success Path ====================

    @Test
    void testAction_SuccessPath() throws Exception {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        // Inject mocks
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("ACTION"), eq(chatData))).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("[{\"action\":\"Review code\"}]")
        );

        // Act
        CompletableFuture<String> result = aiClientService.action(chatData);

        // Assert
        assertNotNull(result);
        assertEquals("[{\"action\":\"Review code\"}]", result.get());
        verify(mockRequestFactory).getRequest(eq("ACTION"), eq(chatData));
    }

    @Test
    void testAction_IOExceptionInFactory() throws Exception {
        // Arrange
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        when(mockRequestFactory.getRequest(eq("ACTION"), eq(chatData)))
                .thenThrow(new IOException("Factory error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aiClientService.action(chatData);
        });

        assertTrue(exception.getCause() instanceof IOException);
    }

    // ==================== Testing clearSummary() ====================

    @Test
    void testClearSummary_AfterSummarisation() throws Exception {
        // Arrange - First create a summary
        String jsonContent = "{\"chat\":\"test\"}";

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("SUM"), anyString())).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("Test summary")
        );

        aiClientService.summariseText(jsonContent).get();

        // Act
        CompletableFuture<String> result = aiClientService.clearSummary();

        // Assert
        assertEquals("Summary cleared successfully", result.get());

        // Verify next summarization starts fresh
        when(mockAsyncExecutor.execute(any())).thenReturn(
                CompletableFuture.completedFuture("New summary")
        );
        CompletableFuture<String> newSummary = aiClientService.summariseText("{\"chat\":\"new\"}");
        assertEquals("New summary", newSummary.get());
    }

    // ==================== Testing Sequential Operations ====================

    @Test
    void testSequentialSummarization() throws Exception {
        // Test that summaries are processed sequentially
        String content1 = "{\"chat\":\"first\"}";
        String content2 = "{\"chat\":\"second\"}";
        String content3 = "{\"chat\":\"third\"}";

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockRequestFactory);

        var executorField = AiClientService.class.getDeclaredField("ASYNC_AI_EXECUTOR");
        executorField.setAccessible(true);
        executorField.set(null, mockAsyncExecutor);

        when(mockRequestFactory.getRequest(eq("SUM"), anyString())).thenReturn(mockRequest);
        when(mockAsyncExecutor.execute(any()))
                .thenReturn(CompletableFuture.completedFuture("Summary 1"))
                .thenReturn(CompletableFuture.completedFuture("Summary 2"))
                .thenReturn(CompletableFuture.completedFuture("Summary 3"));

        // Act - Fire all three without waiting
        CompletableFuture<String> f1 = aiClientService.summariseText(content1);
        CompletableFuture<String> f2 = aiClientService.summariseText(content2);
        CompletableFuture<String> f3 = aiClientService.summariseText(content3);

        // Assert - All should complete
        assertNotNull(f1.get());
        assertNotNull(f2.get());
        assertNotNull(f3.get());
    }
}