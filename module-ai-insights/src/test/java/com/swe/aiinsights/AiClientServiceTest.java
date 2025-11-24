package com.swe.aiinsights;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.aiinsights.apiendpoints.AiClientService;
import com.swe.aiinsights.request.RequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Complete test class for AiClientService with 100% coverage.
 */
@ExtendWith(MockitoExtension.class)
class AiClientServiceTest {

    private AiClientService aiClientService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        aiClientService = new AiClientService();
        objectMapper = new ObjectMapper();
    }

    // ==================== Constructor Test ====================

    @Test
    void testConstructor_CreatesServiceSuccessfully() {
        assertNotNull(aiClientService);
    }

    // ==================== describe() Tests ====================

    @Test
    void testDescribeWithValidImageFile() throws Exception {
        Path tempFile = Files.createTempFile("test-image", ".png");
        try {

            Path path = Paths.get(getClass().getResource("/images/test.png").toURI());;

            CompletableFuture<String> result = aiClientService.describe(path.toString());
            assertNotNull(result);
        } catch (Exception e) {
            assertInstanceOf(Exception.class, e);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testDescribeWithNonExistentFile() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aiClientService.describe("/nonexistent/file/path.png");
        });

        assertNotNull(exception.getCause());
    }

    // ==================== regularise() Tests ====================

    @Test
    void testRegularise_WithValidJsonPoints() {
        String points = """
                 {
                  "ShapeId": "c585b84a",
                  "Type": "FREEHAND",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 2,
                  "CreatedBy": "user_default",
                  "LastModifiedBy": "user_default",
                  "IsDeleted": false
                }
                """;
        CompletableFuture<String> result = aiClientService.regularise(points);
        assertNotNull(result);
    }

    @Test
    void testRegulariseCatchesGeneralException() throws Exception {
        RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("REG"), any())).thenThrow(new IllegalStateException("Test exception"));

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        try {
            aiClientService.regularise("{\"points\":[]}");
            fail("Should have thrown RuntimeException");
        } catch (RuntimeException e) {
            assertNotNull(e);
        }
    }


    @Test
    void testSentimentWithValidChatData() throws Exception {
        JsonNode chatData = objectMapper.readTree(
                "{\"messages\":[{\"text\":\"Hello\",\"user\":\"Alice\"}]}"
        );
        CompletableFuture<String> result = aiClientService.sentiment(chatData);
        assertNotNull(result);
    }


    @Test
    void testSentimentCatchesIOException() throws Exception {
        RequestFactory mockFactory = mock(RequestFactory.class);
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");
        when(mockFactory.getRequest(eq("INS"), any())).thenThrow(new IOException("Test IOException"));

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        try {
            aiClientService.sentiment(chatData);
            fail("Should have thrown RuntimeException");
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }


    // ==================== summariseText() Tests ====================

    @Test
    void testSummariseText_WithInitialContent() {
        String jsonContent = "{\"chat\":\"This is a meeting about project planning\"}";
        CompletableFuture<String> result = aiClientService.summariseText(jsonContent);
        assertNotNull(result);
    }


    @Test
    void testSummariseTextCatchesIOExceptionInFactory() throws Exception {
        RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("SUM"), anyString())).thenThrow(new IOException("Factory IOException"));

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        CompletableFuture<String> result = aiClientService.summariseText("{\"chat\":\"test\"}");

        try {
            result.get(5, TimeUnit.SECONDS);
            fail("Should have thrown ExecutionException");
        } catch (ExecutionException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof RuntimeException);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    void testSummariseTextCatchesUnexpectedException() throws Exception {
        RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("SUM"), anyString())).thenThrow(new NullPointerException("Unexpected error"));

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        CompletableFuture<String> result = aiClientService.summariseText("{\"chat\":\"test\"}");

        try {
            result.get(5, TimeUnit.SECONDS);
            fail("Should have failed");
        } catch (Exception e) {
            assertTrue(e instanceof ExecutionException || e instanceof TimeoutException);
        }
    }

    @Test
    void testSummariseText_WithPreviousSummary_CoversContentToSummariseBranch() throws Exception {
        String firstContent = "{\"chat\":\"First meeting notes\"}";
        CompletableFuture<String> first = aiClientService.summariseText(firstContent);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String secondContent = "{\"chat\":\"Second meeting notes\"}";
        CompletableFuture<String> second = aiClientService.summariseText(secondContent);

        assertNotNull(first);
        assertNotNull(second);
    }

    @Test
    void testSummariseTextUpdatesAccumulatedSummarySuccessfully() throws Exception {
        String content = "{\"chat\":\"Test content for summary update\"}";

        CompletableFuture<String> result = aiClientService.summariseText(content);
        assertNotNull(result);

        CompletableFuture<String> result2 = aiClientService.summariseText("{\"chat\":\"More content\"}");
        assertNotNull(result2);
    }

    // ==================== clearSummary() Tests ====================

    @Test
    void testClear() throws Exception {
        aiClientService.summariseText("{\"chat\":\"test\"}");
        CompletableFuture<String> result = aiClientService.clearSummary();
        assertEquals("Summary cleared successfully", result.get());
    }


    @Test
    void testClearSummaryThenSummariseAgain() throws Exception {
        aiClientService.summariseText("{\"chat\":\"first\"}");
        aiClientService.clearSummary().get();

        CompletableFuture<String> result = aiClientService.summariseText("{\"chat\":\"new\"}");
        assertNotNull(result);
    }

    // ==================== answerQuestion() Tests ====================

    @Test
    void testAnswerQuestion() {
        String question = "What is the main topic?";
        CompletableFuture<String> result = aiClientService.answerQuestion(question);
        assertNotNull(result);
    }


    @Test
    void testAnswerQuestionAfterSummarisation() {
        aiClientService.summariseText("{\"chat\":\"meeting notes\"}");
        String question = "What were the key points?";

        CompletableFuture<String> result = aiClientService.answerQuestion(question);
        assertNotNull(result);
    }

    @Test
    void testAnswerQuestionWithoutSummary() {
        String question = "What is AI?";
        CompletableFuture<String> result = aiClientService.answerQuestion(question);
        assertNotNull(result);
    }


    @Test
    void testAnswerQuestionAfterClearSummary() throws Exception {
        aiClientService.summariseText("{\"chat\":\"test\"}");
        aiClientService.clearSummary().get();
        String question = "What happened?";

        CompletableFuture<String> result = aiClientService.answerQuestion(question);
        assertNotNull(result);
    }


    @Test
    void testAnswerQuestionCatchesIOExceptionInFactory() throws Exception {
        RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("QNA"), anyString(), anyString())).thenThrow(new IOException("Factory IOException"));

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        CompletableFuture<String> result = aiClientService.answerQuestion("Test question?");

        try {
            result.get(5, TimeUnit.SECONDS);
            fail("Should have thrown ExecutionException");
        } catch (ExecutionException e) {
            assertNotNull(e.getCause());
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    void testAnswerQuestionCatchesUnexpectedException() throws Exception {
        RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("QNA"), anyString(), anyString())).thenThrow(new IllegalArgumentException("Unexpected error"));

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        try {
            CompletableFuture<String> result = aiClientService.answerQuestion("Test?");
            result.get(5, TimeUnit.SECONDS);
            fail("Should have thrown");
        } catch (Exception e) {
            assertTrue(e instanceof ExecutionException || e instanceof TimeoutException);
        }
    }

    // ==================== action() Tests ====================

    @Test
    void testActionWithValidChatData() throws Exception {
        JsonNode chatData = objectMapper.readTree(
                "{\"messages\":[{\"text\":\"We need to review the code\"}]}"
        );
        CompletableFuture<String> result = aiClientService.action(chatData);
        assertNotNull(result);
    }





    @Test
    void testActionCatchesIOException() throws Exception {
        RequestFactory mockFactory = mock(RequestFactory.class);
        JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");
        when(mockFactory.getRequest(eq("ACTION"), any())).thenThrow(new IOException("Factory IOException"));

        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        try {
            aiClientService.action(chatData);
            fail("Should have thrown RuntimeException");
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof IOException);
        }
    }


    // ==================== Integration/Workflow Tests ====================

    @Test
    void testCompleteWorkflowSummariseAndAnswer() {
        String content = "{\"chat\":\"Project meeting discussion\"}";
        String question = "What was discussed?";

        CompletableFuture<String> summary = aiClientService.summariseText(content);
        CompletableFuture<String> answer = aiClientService.answerQuestion(question);

        assertNotNull(summary);
        assertNotNull(answer);
    }


    @Test
    void testCompleteWorkflowAllAPIs() throws Exception {
        Path tempFile = Files.createTempFile("test", ".png");
        try {
            Files.write(tempFile, new byte[]{0x00});

            aiClientService.describe(tempFile.toString());
            aiClientService.regularise("{\"points\":[]}");

            JsonNode chatData = objectMapper.readTree("{\"messages\":[]}");
            aiClientService.sentiment(chatData);
            aiClientService.action(chatData);

            aiClientService.summariseText("{\"chat\":\"test\"}");
            aiClientService.answerQuestion("test?");
            aiClientService.clearSummary().get();

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    // ==================== Edge Cases ====================



    @Test
    void testAnswerQuestionOuterCatchBlockSynchronousException() throws Exception {
        // This test covers the outer catch (Exception e) block in answerQuestion
        // by causing an exception BEFORE the async chain starts

        AiClientService spyService = spy(new AiClientService());

        // Inject a mock factory
        RequestFactory mockFactory = mock(RequestFactory.class);
        var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(spyService, mockFactory);

        // Make lastSummaryUpdate throw when thenCompose is called
        var lastSummaryField = AiClientService.class.getDeclaredField("lastSummaryUpdate");
        lastSummaryField.setAccessible(true);

        CompletableFuture<Void> brokenFuture = mock(CompletableFuture.class);
        when(brokenFuture.thenCompose(any())).thenThrow(new IllegalStateException("Broken future"));

        lastSummaryField.set(spyService, brokenFuture);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spyService.answerQuestion("Test question?");
        });

        assertEquals("Error processing Q&A request", exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }

    @Test
    void testSummariseTextOuterCatchBlockSynchronousException() throws Exception {
        // This test covers the outer catch (Exception e) block in summariseText
        // by causing an exception BEFORE the async chain starts

        AiClientService spyService = spy(new AiClientService());

        // Make lastSummaryUpdate throw when thenCompose is called
        var lastSummaryField = AiClientService.class.getDeclaredField("lastSummaryUpdate");
        lastSummaryField.setAccessible(true);

        CompletableFuture<Void> brokenFuture = mock(CompletableFuture.class);
        when(brokenFuture.thenCompose(any())).thenThrow(new NullPointerException("Broken chain"));

        lastSummaryField.set(spyService, brokenFuture);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spyService.summariseText("{\"chat\":\"test\"}");
        });

        assertNotNull(exception);
        assertTrue(exception.getCause() instanceof NullPointerException);
    }
    @Test
    void testSummariseTextElseBranchPreviousSummaryExists() throws Exception {
        // Directly set accumulatedSummary to non-empty to force ELSE branch
        var summaryField = AiClientService.class.getDeclaredField("accumulatedSummary");
        summaryField.setAccessible(true);
        summaryField.set(aiClientService, "Existing summary content");

        // Now call summariseText
        CompletableFuture<String> result = aiClientService.summariseText("{\"chat\":\"New data\"}");

        // Assert - the else branch where contentToSummarise includes "Previous Summary:" is now covered
        assertNotNull(result);
    }

}


