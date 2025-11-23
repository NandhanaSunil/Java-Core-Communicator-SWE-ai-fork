/*
 * -----------------------------------------------------------------------------
 *  File: GeminiServiceTest.java
 *  Owner: Abhirami R Iyer
 *  Roll Number : 112201001
 *  Module : com.swe.aiinsights.data
 * -----------------------------------------------------------------------------
 */

package com.swe.aiinsights;

import com.swe.aiinsights.aiservice.GeminiService;
import com.swe.aiinsights.customexceptions.RateLimitException;
import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.getkeys.GeminiKeyManager;
import com.swe.aiinsights.modeladapter.GeminiAdapter;
import com.swe.aiinsights.response.AiResponse;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for GeminiService with maximum code coverage.
 */
@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private RequestGeneraliser mockRequestGeneraliser;

    @Mock
    private AiResponse mockAiResponse;

    @Mock
    private ResponseBody mockResponseBody;

@Test
void testRunProcessSuccessCoversHappyPath() throws Exception {
    // Mock GeminiKeyManager construction
    try (MockedConstruction<GeminiKeyManager> keyManagerMock = mockConstruction(
            GeminiKeyManager.class,
            (mock, context) -> {
                when(mock.getNumberOfKeys()).thenReturn(3);
                when(mock.getCurrentKey()).thenReturn("test-key-1");
            })) {

        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
            Dotenv localMockDotenv = mock(Dotenv.class);
            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");

            GeminiService service = new GeminiService();

            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            httpClientField.set(service, mockHttpClient);

            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
                    GeminiAdapter.class,
                    (mock, context) -> {
                        when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
                        when(mock.getResponse(any())).thenReturn("AI response text");
                    })) {

                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
                when(mockCall.execute()).thenReturn(mockResponse);
                when(mockResponse.isSuccessful()).thenReturn(true);

                // Act
                AiResponse result = service.runProcess(mockRequestGeneraliser);

                // Assert
                assertNotNull(result);
                verify(mockAiResponse).setResponse("AI response text");
            }
        }
    }
}

    @Test
    void testRunProcessRateLimitHitSwitchesKey() throws Exception {
        try (MockedConstruction<GeminiKeyManager> keyManagerMock = mockConstruction(
                GeminiKeyManager.class,
                (mock, context) -> {
                    when(mock.getNumberOfKeys()).thenReturn(2);
                    when(mock.getCurrentKey())
                            .thenReturn("key1")
                            .thenReturn("key2");
                })) {

            try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
                Dotenv localMockDotenv = mock(Dotenv.class);
                dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
                lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");

                GeminiService service = new GeminiService();

                var httpClientField = GeminiService.class.getDeclaredField("httpClient");
                httpClientField.setAccessible(true);
                httpClientField.set(service, mockHttpClient);

                try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
                        GeminiAdapter.class,
                        (mock, context) -> {
                            when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
                            when(mock.getResponse(any())).thenReturn("Success response");
                        })) {

                    when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
                    when(mockHttpClient.newCall(any())).thenReturn(mockCall);

                    Response rateLimitResponse = mock(Response.class);
                    when(rateLimitResponse.isSuccessful()).thenReturn(false);
                    when(rateLimitResponse.code()).thenReturn(429);

                    when(mockCall.execute())
                            .thenReturn(rateLimitResponse)
                            .thenReturn(mockResponse);
                    when(mockResponse.isSuccessful()).thenReturn(true);

                    // Act
                    AiResponse result = service.runProcess(mockRequestGeneraliser);

                    // Assert
                    assertNotNull(result);

                    // Get the mocked GeminiKeyManager
                    GeminiKeyManager keyManager = keyManagerMock.constructed().get(0);
                    verify(keyManager).setKeyIndex("key1");
                    verify(mockCall, times(2)).execute();
                }
            }
        }
    }

    @Test
    void testRunProcessNonRateLimitErrorThrowsException() throws Exception {
        try (MockedConstruction<GeminiKeyManager> keyManagerMock = mockConstruction(
                GeminiKeyManager.class,
                (mock, context) -> {
                    when(mock.getNumberOfKeys()).thenReturn(1);
                    when(mock.getCurrentKey()).thenReturn("key1");
                })) {

            try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
                Dotenv localMockDotenv = mock(Dotenv.class);
                dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
                lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");

                GeminiService service = new GeminiService();

                var httpClientField = GeminiService.class.getDeclaredField("httpClient");
                httpClientField.setAccessible(true);
                httpClientField.set(service, mockHttpClient);

                try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
                        GeminiAdapter.class,
                        (mock, context) -> {
                            lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}"); // ADD lenient()
                        })) {

                    lenient().when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse); // ADD lenient()
                    when(mockHttpClient.newCall(any())).thenReturn(mockCall);

                    Response errorResponse = mock(Response.class);
                    when(errorResponse.isSuccessful()).thenReturn(false);
                    when(errorResponse.code()).thenReturn(500);

                    when(mockCall.execute()).thenReturn(errorResponse);

                    // Act & Assert
                    assertThrows(RateLimitException.class, () -> {
                        service.runProcess(mockRequestGeneraliser);
                    });
                }
            }
        }
    }

    @Test
    void testRunProcessAllKeysExhaustedThrowsException() throws Exception {
        try (MockedConstruction<GeminiKeyManager> keyManagerMock = mockConstruction(
                GeminiKeyManager.class,
                (mock, context) -> {
                    when(mock.getNumberOfKeys()).thenReturn(2);
                    when(mock.getCurrentKey())
                            .thenReturn("key1")
                            .thenReturn("key2");
                })) {

            try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
                Dotenv localMockDotenv = mock(Dotenv.class);
                dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
                lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");

                GeminiService service = new GeminiService();

                var httpClientField = GeminiService.class.getDeclaredField("httpClient");
                httpClientField.setAccessible(true);
                httpClientField.set(service, mockHttpClient);

                try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
                        GeminiAdapter.class,
                        (mock, context) -> {
                            lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}"); // ADD lenient()
                        })) {

                    lenient().when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse); // ADD lenient()
                    when(mockHttpClient.newCall(any())).thenReturn(mockCall);

                    Response rateLimitResponse = mock(Response.class);
                    when(rateLimitResponse.isSuccessful()).thenReturn(false);
                    when(rateLimitResponse.code()).thenReturn(429);

                    when(mockCall.execute()).thenReturn(rateLimitResponse);

                    // Act & Assert
                    RateLimitException exception = assertThrows(RateLimitException.class, () -> {
                        service.runProcess(mockRequestGeneraliser);
                    });

                    assertTrue(exception.getMessage().contains("All available API keys used"));
                    verify(mockCall, times(2)).execute();
                }
            }
        }
    }
}