package com.swe.aiinsights.apiendpoints;

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

import java.io.IOException;

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

    // ==================== Constructor Tests ====================

//    @Test
//    void testConstructor_LoadsApiKeys() {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1,key2,key3");
//
//            GeminiService service = new GeminiService();
//
//            assertNotNull(service);
//        }
//    }

//    @Test
//    void testConstructor_EmptyKeyListThrowsException() {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("");
//
//            try {
//                GeminiService service = new GeminiService();
//                fail("Expected RuntimeException to be thrown");
//            } catch (RuntimeException e) {
//                assertTrue(e.getMessage().contains("GEMINI_API_KEY_LIST"));
//            }
//        }
//    }

//    @Test
//    void testConstructor_NullKeyListThrowsException() {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn(null);
//
//            try {
//                GeminiService service = new GeminiService();
//                fail("Expected RuntimeException to be thrown");
//            } catch (RuntimeException e) {
//                assertTrue(e.getMessage().contains("GEMINI_API_KEY_LIST"));
//            }
//        }
//    }

//    @Test
//    void testConstructor_MultipleKeysWithSpaces() {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1 , key2 , key3");
//
//            GeminiService service = new GeminiService();
//
//            assertNotNull(service);
//        }
//    }

//    @Test
//    void testConstructor_SingleKey() {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("single-key");
//
//            GeminiService service = new GeminiService();
//
//            assertNotNull(service);
//        }
//    }

//    @Test
//    void testGetKeyList_ParsesCommaSeparatedKeys() {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1,key2,key3,key4,key5");
//
//            GeminiService service = new GeminiService();
//
//            assertNotNull(service);
//        }
//    }

    // ==================== runProcess Tests ====================

//    @Test
//    void testRunProcess_Success() throws Exception {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("test-key-1");
//
//            GeminiService service = new GeminiService();
//
//            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
//                    GeminiAdapter.class,
//                    (mock, context) -> {
//                        lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
//                        lenient().when(mock.getResponse(any())).thenReturn("AI response text");
//                    })) {
//
//                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//                when(mockCall.execute()).thenReturn(mockResponse);
//                when(mockResponse.isSuccessful()).thenReturn(true);
//                lenient().when(mockResponse.body()).thenReturn(mockResponseBody);
//
//                AiResponse result = service.runProcess(mockRequestGeneraliser);
//
//                assertNotNull(result);
//                assertEquals(mockAiResponse, result);
//                verify(mockAiResponse).setResponse("AI response text");
//            }
//        }
//    }

//    @Test
//    void testRunProcess_RateLimitSwitchesToNextKey() throws Exception {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1,key2");
//
//            GeminiService service = new GeminiService();
//
//            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
//                    GeminiAdapter.class,
//                    (mock, context) -> {
//                        lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
//                        lenient().when(mock.getResponse(any())).thenReturn("Success response");
//                    })) {
//
//                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//
//                Response rateLimitResponse = mock(Response.class, withSettings().lenient());
//                lenient().when(rateLimitResponse.isSuccessful()).thenReturn(false);
//                lenient().when(rateLimitResponse.code()).thenReturn(429);
//                lenient().when(rateLimitResponse.body()).thenReturn(mockResponseBody);
//
//                when(mockCall.execute())
//                        .thenReturn(rateLimitResponse)
//                        .thenReturn(mockResponse);
//                when(mockResponse.isSuccessful()).thenReturn(true);
//                lenient().when(mockResponse.body()).thenReturn(mockResponseBody);
//
//                AiResponse result = service.runProcess(mockRequestGeneraliser);
//
//                assertNotNull(result);
//                verify(mockCall, times(2)).execute();
//            }
//        }
//    }

//    @Test
//    void testRunProcess_ThreeKeysSecondSucceeds() throws Exception {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1,key2,key3");
//
//            GeminiService service = new GeminiService();
//
//            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
//                    GeminiAdapter.class,
//                    (mock, context) -> {
//                        lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
//                        lenient().when(mock.getResponse(any())).thenReturn("Success");
//                    })) {
//
//                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//
//                Response rateLimitResponse = mock(Response.class, withSettings().lenient());
//                lenient().when(rateLimitResponse.isSuccessful()).thenReturn(false);
//                lenient().when(rateLimitResponse.code()).thenReturn(429);
//                lenient().when(rateLimitResponse.body()).thenReturn(mockResponseBody);
//
//                when(mockCall.execute())
//                        .thenReturn(rateLimitResponse)
//                        .thenReturn(mockResponse);
//                when(mockResponse.isSuccessful()).thenReturn(true);
//                lenient().when(mockResponse.body()).thenReturn(mockResponseBody);
//
//                AiResponse result = service.runProcess(mockRequestGeneraliser);
//
//                assertNotNull(result);
//                verify(mockCall, times(2)).execute();
//            }
//        }
//    }

//    @Test
//    void testRunProcess_AllKeysExhausted() throws Exception {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1,key2");
//
//            GeminiService service = new GeminiService();
//
//            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
//                    GeminiAdapter.class,
//                    (mock, context) -> {
//                        lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
//                    })) {
//
//                lenient().when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//
//                Response rateLimitResponse = mock(Response.class, withSettings().lenient());
//                lenient().when(rateLimitResponse.isSuccessful()).thenReturn(false);
//                lenient().when(rateLimitResponse.code()).thenReturn(429);
//                lenient().when(rateLimitResponse.body()).thenReturn(mockResponseBody);
//
//                when(mockCall.execute()).thenReturn(rateLimitResponse);
//
//                assertThrows(RateLimitException.class, () -> {
//                    service.runProcess(mockRequestGeneraliser);
//                });
//
//                verify(mockCall, times(2)).execute();
//            }
//        }
//    }

//    @Test
//    void testRunProcess_NonRateLimitError() throws Exception {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1");
//
//            GeminiService service = new GeminiService();
//
//            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
//                    GeminiAdapter.class,
//                    (mock, context) -> {
//                        lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
//                    })) {
//
//                lenient().when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//
//                Response errorResponse = mock(Response.class, withSettings().lenient());
//                lenient().when(errorResponse.isSuccessful()).thenReturn(false);
//                lenient().when(errorResponse.code()).thenReturn(500);
//                lenient().when(errorResponse.body()).thenReturn(mockResponseBody);
//
//                when(mockCall.execute()).thenReturn(errorResponse);
//
//                assertThrows(RateLimitException.class, () -> {
//                    service.runProcess(mockRequestGeneraliser);
//                });
//            }
//        }
//    }
//
//    @Test
//    void testRunProcess_HttpClientThrowsIOException() throws Exception {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1");
//
//            GeminiService service = new GeminiService();
//
//            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
//                    GeminiAdapter.class,
//                    (mock, context) -> {
//                        lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
//                    })) {
//
//                lenient().when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//                when(mockCall.execute()).thenThrow(new IOException("Network error"));
//
//                assertThrows(IOException.class, () -> {
//                    service.runProcess(mockRequestGeneraliser);
//                });
//            }
//        }
//    }
//
//    @Test
//    void testRunProcess_MultipleRequestsWithSameService() throws Exception {
//        try (MockedStatic<Dotenv> dotenvMock = mockStatic(Dotenv.class)) {
//            Dotenv localMockDotenv = mock(Dotenv.class);
//            dotenvMock.when(Dotenv::load).thenReturn(localMockDotenv);
//            lenient().when(localMockDotenv.get("GEMINI_URL")).thenReturn("https://api.gemini.com/");
//            lenient().when(localMockDotenv.get("GEMINI_API_KEY_LIST")).thenReturn("key1");
//
//            GeminiService service = new GeminiService();
//
//            var httpClientField = GeminiService.class.getDeclaredField("httpClient");
//            httpClientField.setAccessible(true);
//            httpClientField.set(service, mockHttpClient);
//
//            try (MockedConstruction<GeminiAdapter> adapterMock = mockConstruction(
//                    GeminiAdapter.class,
//                    (mock, context) -> {
//                        lenient().when(mock.buildRequest(any())).thenReturn("{\"request\":\"body\"}");
//                        lenient().when(mock.getResponse(any()))
//                                .thenReturn("Response 1")
//                                .thenReturn("Response 2")
//                                .thenReturn("Response 3");
//                    })) {
//
//                when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
//                when(mockHttpClient.newCall(any())).thenReturn(mockCall);
//                when(mockCall.execute()).thenReturn(mockResponse);
//                when(mockResponse.isSuccessful()).thenReturn(true);
//                lenient().when(mockResponse.body()).thenReturn(mockResponseBody);
//
//                service.runProcess(mockRequestGeneraliser);
//                service.runProcess(mockRequestGeneraliser);
//                service.runProcess(mockRequestGeneraliser);
//
//                verify(mockCall, times(3)).execute();
//            }
//        }
//    }
@Test
void testRunProcess_Success_CoversHappyPath() throws Exception {
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
    void testRunProcess_RateLimitHit_SwitchesKey() throws Exception {
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
    void testRunProcess_NonRateLimitError_ThrowsException() throws Exception {
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
    void testRunProcess_AllKeysExhausted_ThrowsException() throws Exception {
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

    @Test
    void testRunProcess_AttemptIncrementAndLoop() throws Exception {
        try (MockedConstruction<GeminiKeyManager> keyManagerMock = mockConstruction(
                GeminiKeyManager.class,
                (mock, context) -> {
                    when(mock.getNumberOfKeys()).thenReturn(3);
                    when(mock.getCurrentKey())
                            .thenReturn("key1")
                            .thenReturn("key2")
                            .thenReturn("key3");
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
                            when(mock.getResponse(any())).thenReturn("Final success");
                        })) {

                    when(mockRequestGeneraliser.getAiResponse()).thenReturn(mockAiResponse);
                    when(mockHttpClient.newCall(any())).thenReturn(mockCall);

                    Response rateLimitResponse = mock(Response.class);
                    when(rateLimitResponse.isSuccessful()).thenReturn(false);
                    when(rateLimitResponse.code()).thenReturn(429);

                    when(mockCall.execute())
                            .thenReturn(rateLimitResponse)
                            .thenReturn(rateLimitResponse)
                            .thenReturn(mockResponse);
                    when(mockResponse.isSuccessful()).thenReturn(true);

                    // Act
                    AiResponse result = service.runProcess(mockRequestGeneraliser);

                    // Assert
                    assertNotNull(result);
                    verify(mockCall, times(3)).execute();

                    GeminiKeyManager keyManager = keyManagerMock.constructed().get(0);
                    verify(keyManager, times(2)).setKeyIndex(anyString());
                }
            }
        }
    }
}