/*
 * -----------------------------------------------------------------------------
 *  File: AsyncAiExecutor.java
 *  Owner: Abhirami R Iyer
 *  Roll Number : 112201001
 *  Module : com.swe.aiinsights
 * -----------------------------------------------------------------------------
 */

package com.swe.aiinsights;

import com.swe.aiinsights.aiservice.LlmService;
import com.swe.aiinsights.apiendpoints.AsyncAiExecutor;
import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.request.AiRequestable;
import com.swe.aiinsights.response.AiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Complete test class for AsyncAiExecutor with 100% coverage
 */
@ExtendWith(MockitoExtension.class)
class AsyncAiExecutorTest {

    private AsyncAiExecutor asyncAiExecutor;

    @Mock
    private AiRequestable mockRequest;

    @Mock
    private LlmService mockLlmService;

    @Mock
    private AiResponse mockAiResponse;

    @BeforeEach
    void setUp() {
        asyncAiExecutor = new AsyncAiExecutor();
    }

    // ==================== SUCCESS PATH - Covers the return response lines ====================

    @Test
    void testExecuteSuccessPathReturnsFormattedResponse() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("SUM");
        when(mockRequest.getContext()).thenReturn("test prompt");
        when(mockRequest.getInput()).thenReturn("test data");
        when(mockAiResponse.getResponse()).thenReturn("AI raw response");

        // Inject mock LlmService
        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert
        assertNotNull(result);
        String response = result.get(); // This will cover the return response line
        assertEquals("AI raw response", response);
        verify(mockLlmService).runProcess(any(RequestGeneraliser.class));
    }


    @Test
    void testExecuteIOExceptionInRunProcess() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("DESC");
        when(mockRequest.getContext()).thenReturn("test");
        when(mockRequest.getInput()).thenReturn("data");

        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        // Make runProcess throw IOException
        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenThrow(new IOException("Service failed"));

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert - Covers IOException catch block
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertInstanceOf(IOException.class, exception.getCause().getCause());
    }

    @Test
    void testExecute_UnexpectedExceptionInRunProcess() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("QNA");
        when(mockRequest.getContext()).thenReturn("test");
        when(mockRequest.getInput()).thenReturn("data");

        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        // Make runProcess throw generic exception
        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenThrow(new NullPointerException("Null error"));

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert - Covers generic Exception catch block
        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertInstanceOf(RuntimeException.class, exception.getCause());
    }


    @Test
    void testExecuteMultipleConcurrentRequests() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("SUM");
        when(mockRequest.getContext()).thenReturn("test");
        when(mockRequest.getInput()).thenReturn("test data");
        when(mockAiResponse.getResponse()).thenReturn("Response");

        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Act - Execute multiple requests
        CompletableFuture<String> result1 = asyncAiExecutor.execute(mockRequest);
        CompletableFuture<String> result2 = asyncAiExecutor.execute(mockRequest);
        CompletableFuture<String> result3 = asyncAiExecutor.execute(mockRequest);

        // Assert - All complete successfully
        assertEquals("Response", result1.get());
        assertEquals("Response", result2.get());
        assertEquals("Response", result3.get());
    }
}