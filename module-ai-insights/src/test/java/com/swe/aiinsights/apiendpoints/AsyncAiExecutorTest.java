package com.swe.aiinsights.apiendpoints;

import com.swe.aiinsights.aiservice.LlmService;
import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.request.AiRequestable;
import com.swe.aiinsights.response.AiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
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
    void testExecute_SuccessPath_ReturnsFormattedResponse() throws Exception {
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
    void testExecute_SuccessPath_DESC_Type() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("DESC");
        when(mockRequest.getContext()).thenReturn("describe image");
        when(mockRequest.getInput()).thenReturn("image_data");
        when(mockAiResponse.getResponse()).thenReturn("Image contains a cat");

        // Inject mock LlmService
        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert
        String response = result.get();
        assertEquals("Image contains a cat", response);
    }

    @Test
    void testExecute_SuccessPath_REG_Type() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("REG");
        when(mockRequest.getContext()).thenReturn("regularize");
        when(mockRequest.getInput()).thenReturn("{\"ShapeId\":\"1\",\"Type\":\"Circle\",\"Points\":[[0,0]],\"Color\":\"red\",\"Thickness\":1,\"CreatedBy\":\"user\",\"LastModifiedBy\":\"user\",\"IsDeleted\":false}");
        when(mockAiResponse.getResponse()).thenReturn("{\"type\":\"Circle\",\"Points\":[[0,0],[10,10]]}");

        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert
        String response = result.get();
        assertNotNull(response);
    }
    @Test
    void testExecute_SuccessPath_INS_Type() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("INS");
        when(mockRequest.getContext()).thenReturn("insights");
        when(mockRequest.getInput()).thenReturn("chat data");
        when(mockAiResponse.getResponse()).thenReturn("[0.5, 0.7, 0.3]");

        // Inject mock LlmService
        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert
        String response = result.get();
        assertEquals("[0.5, 0.7, 0.3]", response);
    }

    @Test
    void testExecute_SuccessPath_ACTION_Type() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("ACTION");
        when(mockRequest.getContext()).thenReturn("action items");
        when(mockRequest.getInput()).thenReturn("meeting notes");
        when(mockAiResponse.getResponse()).thenReturn("[{\"task\":\"Review code\"}]");

        // Inject mock LlmService
        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert
        String response = result.get();
        assertEquals("[{\"task\":\"Review code\"}]", response);
    }

    @Test
    void testExecute_SuccessPath_QNA_Type() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("QNA");
        when(mockRequest.getContext()).thenReturn("question");
        when(mockRequest.getInput()).thenReturn("What is AI?");
        when(mockAiResponse.getResponse()).thenReturn("AI is artificial intelligence");

        // Inject mock LlmService
        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Act
        CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

        // Assert
        String response = result.get();
        assertEquals("AI is artificial intelligence", response);
    }

    // ==================== EXCEPTION PATHS ====================

    @Test
    void testExecute_IOExceptionInFormatOutput() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("REG");
        when(mockRequest.getContext()).thenReturn("test");
        when(mockRequest.getInput()).thenReturn("{\"points\":[]}");

        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Mock formatOutput to throw IOException
        try (MockedConstruction<RequestGeneraliser> generaliserMock = mockConstruction(
                RequestGeneraliser.class,
                (mock, context) -> {
                    when(mock.formatOutput(any())).thenThrow(new IOException("Format error"));
                })) {

            // Act
            CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

            // Assert - Covers IOException catch block
            ExecutionException exception = assertThrows(ExecutionException.class, result::get);
            assertTrue(exception.getCause() instanceof RuntimeException);
            assertTrue(exception.getCause().getCause() instanceof IOException);
        }
    }

    @Test
    void testExecute_UnexpectedExceptionInFormatOutput() throws Exception {
        // Arrange
        when(mockRequest.getReqType()).thenReturn("SUM");
        when(mockRequest.getContext()).thenReturn("test");
        when(mockRequest.getInput()).thenReturn("data");

        Field llmServiceField = AsyncAiExecutor.class.getDeclaredField("llmService");
        llmServiceField.setAccessible(true);
        llmServiceField.set(asyncAiExecutor, mockLlmService);

        when(mockLlmService.runProcess(any(RequestGeneraliser.class)))
                .thenReturn(mockAiResponse);

        // Mock formatOutput to throw generic Exception
        try (MockedConstruction<RequestGeneraliser> generaliserMock = mockConstruction(
                RequestGeneraliser.class,
                (mock, context) -> {
                    when(mock.formatOutput(any()))
                            .thenThrow(new RuntimeException("Unexpected error"));
                })) {

            // Act
            CompletableFuture<String> result = asyncAiExecutor.execute(mockRequest);

            // Assert - Covers generic Exception catch block
            ExecutionException exception = assertThrows(ExecutionException.class, result::get);
            assertTrue(exception.getCause() instanceof RuntimeException);
        }
    }

    @Test
    void testExecute_IOExceptionInRunProcess() throws Exception {
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
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getCause() instanceof IOException);
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
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    // ==================== MULTIPLE CONCURRENT REQUESTS ====================

    @Test
    void testExecute_MultipleConcurrentRequests() throws Exception {
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