/*
 * -----------------------------------------------------------------------------
 *  File: OllamaAndOrchestratorServiceTest.java
 *  Owner: Nandhana Sunil
 *  Roll Number : 112201008
 *  Module : com.swe.aiinsights
 * -----------------------------------------------------------------------------
 */

package com.swe.aiinsights;

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
 * Tests for OllamaService and LlmOrchestratorService
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

    // test constructor of Ollama service
    @Test
    void testOllamaServiceConstructor() {
        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            dotenvMock.when(Dotenv::load).thenReturn(mockDotenv);
            lenient().when(mockDotenv.get("OLLAMA_URL")).thenReturn("http://localhost:11434/api/generate");

            final OllamaService service = new OllamaService();
            assertNotNull(service);
        }
    }

    //test successful execution of run process in Ollama Service
    @Test
    void testOllamaServiceRunProcessSuccess() throws Exception {
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
                        when(mock.buildRequest(any())).thenReturn("{\"model\":\"gemma3\",\"prompt\":\"test\"}");
                        when(mock.getResponse(any())).thenReturn("Ollama response");
                    })) {

                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
                when(mockCall.execute()).thenReturn(mockResponse);
                when(mockResponse.isSuccessful()).thenReturn(true);
                when(mockResponse.code()).thenReturn(200);
                AiResponse result = service.runProcess(mockRequestGeneraliser);

                assertNotNull(result);
                verify(mockAiResponse).setResponse("Ollama response");
            }
        }
    }

    // test failure of Ollama service run process
    @Test
    void testOllamaServiceRunProcessFailure() throws Exception {
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

                assertThrows(IOException.class, () -> {
                    service.runProcess(mockRequestGeneraliser);
                });
            }
        }
    }

    // LlmOrchestratorService Tests

    //check creation of Orchestrator service
    @Test
    void testOrchestratorConstructorValidServices() {
        final List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        final LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);
        assertNotNull(orchestrator);
    }

    // exception handling
    @Test
    void testOrchestratorConstructorNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LlmOrchestratorService(null);
        });
    }

    @Test
    void testOrchestratorConstructorEmptyServices() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LlmOrchestratorService(Collections.emptyList());
        });
    }

    // first service will be successful
    @Test
    void testOrchestratorRunProcessFirstService() throws IOException {
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);
        when(mockLlmService1.runProcess(any())).thenReturn(mockAiResponse);
        AiResponse result = orchestrator.runProcess(mockRequestGeneraliser);
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
        List<LlmService> services = Arrays.asList(mockLlmService1, mockLlmService2);
        LlmOrchestratorService orchestrator = new LlmOrchestratorService(services);

        when(mockLlmService1.runProcess(any())).thenThrow(new RateLimitException("Rate limit 1"));
        when(mockLlmService2.runProcess(any())).thenThrow(new RateLimitException("Rate limit 2"));

        assertThrows(IOException.class, () -> {
            orchestrator.runProcess(mockRequestGeneraliser);
        });

        verify(mockLlmService1).runProcess(mockRequestGeneraliser);
        verify(mockLlmService2).runProcess(mockRequestGeneraliser);
    }
}