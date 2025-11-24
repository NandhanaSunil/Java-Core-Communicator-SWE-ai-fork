/*
 * -----------------------------------------------------------------------------
 *  File: AiClientServiceTest.java
 *  Owner: Abhirami R Iyer
 *  Roll Number : 112201001
 *  Module : com.swe.aiinsights
 * -----------------------------------------------------------------------------
 */

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests covering the AiClientService.
 */
@ExtendWith(MockitoExtension.class)
class AiClientServiceTest {
    /**
     * AiClientService to test the class.
     */
    private AiClientService aiClientService;
    /**
     * Object mapper to build Json if any.
     */
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        aiClientService = new AiClientService();
        objectMapper = new ObjectMapper();
    }


    /**
     * Tests to check if the client service works for description.
     * @throws Exception if there's an issue in reading the image file
     */
    @Test
    void testDescribeWithValidImageFile() throws Exception {
        final Path tempFile = Files.createTempFile("test-image", ".png");
        try {
            Files.write(tempFile, new byte[]{0x00, 0x01, 0x02});
            final CompletableFuture<String> result = aiClientService.describe(
                    tempFile.toString());
            assertNotNull(result);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Test to check if an exception is thrown on invalid image input.
     */
    @Test
    void testDescribeWithNonExistentFile() {
        final RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            aiClientService.describe("/nonexistent/file/path.png");
        });
        assertNotNull(exception.getCause());
        assertInstanceOf(IOException.class, exception.getCause());
    }


    /**
     * Test to check if regulariser works with valid json.
     */
    @Test
    void testRegulariseWithValidJsonPoints() {
        final String points = "{\"points\":[{\"x\":10,\"y\":20}]}";
        final CompletableFuture<String> result = aiClientService.regularise(points);
        assertNotNull(result);
    }

    /**
     * Test to check if the regulariser throws runtime exception if any.
     * @throws Exception part of regularise function of client service
     */
    @Test
    void testRegulariseCatchesException() throws Exception {
        final RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("REG"), any())).thenThrow(new IllegalStateException("Test exception"));

        final var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        assertThrows(RuntimeException.class, () -> aiClientService.regularise("{\"points\":[]}"));
    }


    @Test
    void testSentimentWithValidChatData() throws Exception {
        final JsonNode chatData = objectMapper.readTree("{\"messages\":[{\"text\":\"Hello\"}]}");
        final CompletableFuture<String> result = aiClientService.sentiment(chatData);
        assertNotNull(result);
    }

    @Test
    void testSentimentCatchesIOException() throws Exception {
        final RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("INS"), any())).thenThrow(new IOException("Test IOException"));

        final var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        assertThrows(RuntimeException.class, () -> aiClientService.sentiment(null));
    }



    @Test
    void testSummariseTextInitialSummary() {
        final CompletableFuture<String> result = aiClientService.summariseText("{\"chat\":\"Meeting notes\"}");
        assertNotNull(result);
    }

    @Test
    void testSummariseTextWithPreviousSummary() throws Exception {
        // Set accumulated summary to simulate existing summary
        final var summaryField = AiClientService.class.getDeclaredField("accumulatedSummary");
        summaryField.setAccessible(true);
        summaryField.set(aiClientService, "Existing summary");

        final CompletableFuture<String> result = aiClientService.summariseText("{\"chat\":\"New data\"}");
        assertNotNull(result);
    }

    @Test
    void testSummariseTextCatchesException() throws Exception {
        final RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("SUM"), anyString())).thenThrow(new IOException("Factory IOException"));

        final var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        final CompletableFuture<String> result = aiClientService.summariseText("{\"chat\":\"test\"}");

        final int timeout = 5;
        assertThrows(ExecutionException.class, () -> result.get(timeout, TimeUnit.SECONDS));
    }


    @Test
    void testClearSummaryReturnsSuccessMessage() throws Exception {
        final CompletableFuture<String> result = aiClientService.clearSummary();
        assertEquals("Summary cleared successfully", result.get());
    }


    @Test
    void testAnswerQuestionWithSummary() {
        aiClientService.summariseText("{\"chat\":\"meeting notes\"}");
        final CompletableFuture<String> result = aiClientService.answerQuestion("What were the key points?");
        assertNotNull(result);
    }

    @Test
    void testAnswerQuestionWithNullAccumulatedSummary() throws Exception {
        // Set summary to null to test else branch
        final var summaryField = AiClientService.class.getDeclaredField("accumulatedSummary");
        summaryField.setAccessible(true);
        summaryField.set(aiClientService, null);

        final CompletableFuture<String> result = aiClientService.answerQuestion("What is AI?");
        assertNotNull(result);
    }

    @Test
    void testAnswerQuestionCatchesException() throws Exception {
        final RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("QNA"), anyString(), anyString()))
                .thenThrow(new IOException("Factory IOException"));

        final var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        final int timeout = 5;
        final CompletableFuture<String> result = aiClientService.answerQuestion("Test?");
        assertThrows(ExecutionException.class, () -> result.get(timeout, TimeUnit.SECONDS));
    }


    @Test
    void testActionWithValidChatData() throws Exception {
        final JsonNode chatData = objectMapper.readTree("{\"messages\":[{\"text\":\"Review code\"}]}");
        final CompletableFuture<String> result = aiClientService.action(chatData);
        assertNotNull(result);
    }

    @Test
    void testActionCatchesIOException() throws Exception {
        final RequestFactory mockFactory = mock(RequestFactory.class);
        when(mockFactory.getRequest(eq("ACTION"), any())).thenThrow(new IOException("Factory IOException"));

        final var factoryField = AiClientService.class.getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(aiClientService, mockFactory);

        assertThrows(RuntimeException.class, () -> aiClientService.action(null));
    }
}