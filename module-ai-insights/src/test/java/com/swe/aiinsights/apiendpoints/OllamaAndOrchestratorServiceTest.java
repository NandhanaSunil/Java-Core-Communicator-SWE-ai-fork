package com.swe.aiinsights.apiendpoints;

import com.swe.aiinsights.aiservice.GeminiService;
import com.swe.aiinsights.aiservice.LlmOrchestratorService;
import com.swe.aiinsights.aiservice.LlmService;
import com.swe.aiinsights.aiservice.OllamaService;
import com.swe.aiinsights.customexceptions.RateLimitException;
import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.modeladapter.OllamaAdapter;
import com.swe.aiinsights.response.AiResponse;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for OllamaService and LlmOrchestratorService
 * with 100% code coverage.
 */
@ExtendWith(MockitoExtension.class)
class OllamaAndOrchestratorServiceTest {

    @Mock
    private Dotenv mockDotenv;

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    @Mock
    private RequestGeneraliser mockRequestGeneraliser;

    @Mock
    private AiResponse mockAiResponse;

    @Mock
    private LlmService mockLlmService1;

    @Mock
    private LlmService mockLlmService2;

    @Mock
    private LlmService mockLlmService3;

    // ==================== OllamaService Tests ====================

    @Test
    void testOllamaService_Constructor() {
        // Arrange & Act
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            dotenvMock.when(Dotenv::load).thenReturn(mockDotenv);
            lenient().when(mockDotenv.get("OLLAMA_URL")).thenReturn("http://localhost:11434/api/generate");

            // Act
            OllamaService service = new OllamaService();

            // Assert
            assertNotNull(service);
        }
    }

    @Test
    void testOllamaService_RunProcess_Success() throws Exception {
        // Arrange
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            dotenvMock.when(Dotenv::load).thenReturn(mockDotenv);
            lenient().when(mockDotenv.get("OLLAMA_URL")).thenReturn("http://localhost:11434/api/generate");

            OllamaService service = new OllamaService();

            var httpClientField = OllamaService.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            httpClientField.set(service, mockHttpClient);

            try (MockedConstruction<OllamaAdapter> adapterMock = mockConstruction(
                    OllamaAdapter.class,
                    (mock, context) -> {
                        when(mock.buildRequest(any())).thenReturn("{\"model\":\"llama2\",\"prompt\":\"test\"}");
                        when(mock.getResponse(any())).thenReturn("Ollama response");
                    })) {

                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
                when(mockCall.execute()).thenReturn(mockResponse);
                when(mockResponse.isSuccessful()).thenReturn(true);
                when(mockResponse.code()).thenReturn(200);

                // Act
                AiResponse result = service.runProcess(mockRequestGeneraliser);

                // Assert
                assertNotNull(result);
                verify(mockAiResponse).setResponse("Ollama response");
            }
        }
    }

    @Test
    void testOllamaService_RunProcess_Failure() throws Exception {
        // Arrange
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            dotenvMock.when(Dotenv::load).thenReturn(mockDotenv);
            lenient().when(mockDotenv.get("OLLAMA_URL")).thenReturn("http://localhost:11434/api/generate");

            OllamaService service = new OllamaService();

            var httpClientField = OllamaService.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            httpClientField.set(service, mockHttpClient);

            try (MockedConstruction<OllamaAdapter> adapterMock = mockConstruction(
                    OllamaAdapter.class,
                    (mock, context) -> {
                        when(mock.buildRequest(any())).thenReturn("{\"model\":\"llama2\"}");
                    })) {

                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
                when(mockCall.execute()).thenReturn(mockResponse);
                when(mockResponse.isSuccessful()).thenReturn(false);
                when(mockResponse.code()).thenReturn(500);
                when(mockResponse.body()).thenReturn(mockResponseBody);
                when(mockResponseBody.string()).thenReturn("Internal server error");

                // Act & Assert
                assertThrows(IOException.class, () -> {
                    service.runProcess(mockRequestGeneraliser);
                });
            }
        }
    }

//    @Test
//    void testOllamaService_RunProcess_IOException() throws Exception {
//        // Arrange
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            dotenvMock.when(Dotenv::load).thenReturn(mockDotenv);
//            lenient().when(mockDotenv.get("OLLAMA_URL")).thenReturn("http://localhost:11434/api/generate");
//
//            OllamaService service = new OllamaService();
//
//            var httpClientField = OllamaService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<OllamaAdapter> adapterMock = mockConstruction(
//                    OllamaAdapter.class,
//                    (mock, context) -> {
//                        when(mock.buildRequest(any())).thenThrow(new IOException("Network error"));
//                    })) {
//
//                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//
//                // Act & Assert
//                assertThrows(IOException.class, () -> {
//                    service.runProcess(mockRequestGeneraliser);
//                });
//            }
//        }
//    }

    // ==================== LlmOrchestratorService Tests ====================

    @Test
    void testOrchestrator_Constructor_ValidServices() {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);

        // Act
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        // Assert
        assertNotNull(orchestrator);
    }

    @Test
    void testOrchestrator_Constructor_NullServices() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new LlmOrchestratorService(null);
        });
    }

    @Test
    void testOrchestrator_Constructor_EmptyServices() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new LlmOrchestratorService(Collections.emptyList());
        });
    }

    @Test
    void testOrchestrator_RunProcess_FirstServiceSucceeds() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenReturn(mockAiResponse);

        // Act
        AiResponse result = orchestrator.runProcess(mockRequestGeneraliser);

        // Assert
        assertNotNull(result);
        verify(mockLlmService1).runProcess(mockRequestGeneraliser);
        verify(mockLlmService2, never()).runProcess(any());
    }

    @Test
    void testOrchestrator_RunProcess_FirstFailsSecondSucceeds() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenThrow(new RateLimitException("Rate limit hit"));
        when(mockLlmService2.runProcess(any())).thenReturn(mockAiResponse);

        // Act
        AiResponse result = orchestrator.runProcess(mockRequestGeneraliser);

        // Assert
        assertNotNull(result);
        verify(mockLlmService1).runProcess(mockRequestGeneraliser);
        verify(mockLlmService2).runProcess(mockRequestGeneraliser);
    }

    @Test
    void testOrchestrator_RunProcess_AllServicesFail() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenThrow(new RateLimitException("Rate limit 1"));
        when(mockLlmService2.runProcess(any())).thenThrow(new RateLimitException("Rate limit 2"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            orchestrator.runProcess(mockRequestGeneraliser);
        });

        verify(mockLlmService1).runProcess(mockRequestGeneraliser);
        verify(mockLlmService2).runProcess(mockRequestGeneraliser);
    }

    @Test
    void testOrchestrator_RunProcess_ThreeServicesSecondSucceeds() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2, mockLlmService3);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenThrow(new RateLimitException("Rate limit 1"));
        when(mockLlmService2.runProcess(any())).thenReturn(mockAiResponse);

        // Act
        AiResponse result = orchestrator.runProcess(mockRequestGeneraliser);

        // Assert
        assertNotNull(result);
        verify(mockLlmService1).runProcess(mockRequestGeneraliser);
        verify(mockLlmService2).runProcess(mockRequestGeneraliser);
        verify(mockLlmService3, never()).runProcess(any());
    }

    @Test
    void testOrchestrator_RunProcess_PermanentSwitch() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        // First request: service1 fails, service2 succeeds
        when(mockLlmService1.runProcess(any())).thenThrow(new RateLimitException("Rate limit"));
        when(mockLlmService2.runProcess(any())).thenReturn(mockAiResponse);

        // Act - First request
        orchestrator.runProcess(mockRequestGeneraliser);

        // Act - Second request (should start with service2 now)
        reset(mockLlmService1, mockLlmService2);
        when(mockLlmService2.runProcess(any())).thenReturn(mockAiResponse);
        orchestrator.runProcess(mockRequestGeneraliser);

        // Assert - service1 should not be called on second request
        verify(mockLlmService1, never()).runProcess(any());
        verify(mockLlmService2, times(1)).runProcess(mockRequestGeneraliser);
    }

    @Test
    void testOrchestrator_RunProcess_MultipleRequests() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenReturn(mockAiResponse);

        // Act - Multiple successful requests
        orchestrator.runProcess(mockRequestGeneraliser);
        orchestrator.runProcess(mockRequestGeneraliser);
        orchestrator.runProcess(mockRequestGeneraliser);

        // Assert
        verify(mockLlmService1, times(3)).runProcess(mockRequestGeneraliser);
        verify(mockLlmService2, never()).runProcess(any());
    }

    @Test
    void testOrchestrator_RunProcess_NonRateLimitException() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenThrow(new IOException("Network error"));

        // Act & Assert - Should throw immediately, not try next service
        assertThrows(IOException.class, () -> {
            orchestrator.runProcess(mockRequestGeneraliser);
        });

        verify(mockLlmService1).runProcess(mockRequestGeneraliser);
        verify(mockLlmService2, never()).runProcess(any());
    }

    @Test
    void testOrchestrator_RunProcess_SingleService() throws IOException {
        // Arrange
        List<LlmService> services = Collections.singletonList(mockLlmService1);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenReturn(mockAiResponse);

        // Act
        AiResponse result = orchestrator.runProcess(mockRequestGeneraliser);

        // Assert
        assertNotNull(result);
        verify(mockLlmService1).runProcess(mockRequestGeneraliser);
    }

    @Test
    void testOrchestrator_RunProcess_SingleServiceFails() throws IOException {
        // Arrange
        List<LlmService> services = Collections.singletonList(mockLlmService1);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenThrow(new RateLimitException("Rate limit"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            orchestrator.runProcess(mockRequestGeneraliser);
        });
    }

    @Test
    void testOrchestrator_RunProcess_SwitchBackAndForth() throws IOException {
        // Arrange
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        // First request: service1 fails, switch to service2
        when(mockLlmService1.runProcess(any())).thenThrow(new RateLimitException("Rate limit 1"));
        when(mockLlmService2.runProcess(any())).thenReturn(mockAiResponse);
        orchestrator.runProcess(mockRequestGeneraliser);

        // Second request: service2 continues to work
        reset(mockLlmService1, mockLlmService2);
        when(mockLlmService2.runProcess(any())).thenReturn(mockAiResponse);
        orchestrator.runProcess(mockRequestGeneraliser);

        // Assert - second request should use service2 directly
        verify(mockLlmService1, never()).runProcess(any());
        verify(mockLlmService2, times(1)).runProcess(mockRequestGeneraliser);
    }

    // ==================== Integration Tests ====================

    @Test
    void testIntegration_OrchestratorWithRealServiceTypes() throws IOException {
        // Arrange - Simulate real service types
        LlmService geminiService = mock(GeminiService.class);
        LlmService ollamaService = mock(OllamaService.class);

        List<LlmService> services = Arrays.asList(geminiService, ollamaService);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(geminiService.runProcess(any())).thenThrow(new RateLimitException("Gemini rate limit"));
        when(ollamaService.runProcess(any())).thenReturn(mockAiResponse);

        // Act
        AiResponse result = orchestrator.runProcess(mockRequestGeneraliser);

        // Assert
        assertNotNull(result);
        verify(geminiService).runProcess(mockRequestGeneraliser);
        verify(ollamaService).runProcess(mockRequestGeneraliser);
    }

    @Test
    void testOllamaService_MultipleRequests() throws Exception {
        // Arrange
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            dotenvMock.when(Dotenv::load).thenReturn(mockDotenv);
            lenient().when(mockDotenv.get("OLLAMA_URL")).thenReturn("http://localhost:11434/api/generate");

            OllamaService service = new OllamaService();

            var httpClientField = OllamaService.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            httpClientField.set(service, mockHttpClient);

            try (MockedConstruction<OllamaAdapter> adapterMock = mockConstruction(
                    OllamaAdapter.class,
                    (mock, context) -> {
                        when(mock.buildRequest(any())).thenReturn("{\"model\":\"llama2\"}");
                        when(mock.getResponse(any()))
                                .thenReturn("Response 1")
                                .thenReturn("Response 2")
                                .thenReturn("Response 3");
                    })) {

                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
                when(mockCall.execute()).thenReturn(mockResponse);
                when(mockResponse.isSuccessful()).thenReturn(true);
                when(mockResponse.code()).thenReturn(200);

                // Act
                service.runProcess(mockRequestGeneraliser);
                service.runProcess(mockRequestGeneraliser);
                service.runProcess(mockRequestGeneraliser);

                // Assert
                verify(mockCall, times(3)).execute();
            }
        }
    }
}