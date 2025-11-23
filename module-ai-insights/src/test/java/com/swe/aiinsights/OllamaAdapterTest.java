package com.swe.aiinsights;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.modeladapter.OllamaAdapter;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for OllamaAdapter with 100% code coverage.
 */
@ExtendWith(MockitoExtension.class)
class OllamaAdapterTest {

    private OllamaAdapter adapter;

    @Mock
    private RequestGeneraliser mockRequest;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    @BeforeEach
    void setUp() {
        adapter = new OllamaAdapter();
    }

    // ==================== buildRequest Tests ====================

    @Test
    void testBuildRequest_WithTextData() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("Summarize: ");
        when(mockRequest.getTextData()).thenReturn("Text to process");
        when(mockRequest.getImgData()).thenReturn(null);

        String result = adapter.buildRequest(mockRequest);

        assertNotNull(result);
        verify(mockRequest).getPrompt();
        verify(mockRequest, atLeastOnce()).getTextData(); // CHANGE THIS
        verify(mockRequest).getImgData();
    }

    @Test
    void testBuildRequest_WithImageData() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("Describe image: ");
        when(mockRequest.getTextData()).thenReturn("Some text");
        when(mockRequest.getImgData()).thenReturn("base64ImageData");

        String result = adapter.buildRequest(mockRequest);

        assertNotNull(result);
        verify(mockRequest).getPrompt();
        verify(mockRequest).getTextData();
        verify(mockRequest, atLeastOnce()).getImgData(); // CHANGE THIS
    }
    @Test
    void testBuildRequest_ContainsCorrectOptions() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("Test");
        when(mockRequest.getTextData()).thenReturn("Data");
        when(mockRequest.getImgData()).thenReturn(null);

        String result = adapter.buildRequest(mockRequest);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result);

        // Verify options
        assertTrue(jsonNode.has("options"));
        JsonNode options = jsonNode.get("options");
        assertEquals(16384, options.get("num_ctx").asInt());
        assertEquals(0.2, options.get("temperature").asDouble(), 0.001);
        assertEquals(0.9, options.get("top_p").asDouble(), 0.001);
    }

    @Test
    void testBuildRequest_VerifyModelName() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("Test");
        when(mockRequest.getTextData()).thenReturn("Data");
        when(mockRequest.getImgData()).thenReturn(null);

        String result = adapter.buildRequest(mockRequest);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result);

        assertEquals("gemma3", jsonNode.get("model").asText());
    }

    @Test
    void testBuildRequest_VerifyStreamIsFalse() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("Test");
        when(mockRequest.getTextData()).thenReturn("Data");
        when(mockRequest.getImgData()).thenReturn(null);

        String result = adapter.buildRequest(mockRequest);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result);

        assertFalse(jsonNode.get("stream").asBoolean());
    }

    @Test
    void testBuildRequest_EmptyPrompt() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("");
        when(mockRequest.getTextData()).thenReturn("Text");
        when(mockRequest.getImgData()).thenReturn(null);

        String result = adapter.buildRequest(mockRequest);

        assertNotNull(result);
        assertTrue(result.contains("Text"));
    }

    @Test
    void testBuildRequest_NullTextData() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("Test");
        when(mockRequest.getTextData()).thenReturn(null);
        when(mockRequest.getImgData()).thenReturn(null);

        String result = adapter.buildRequest(mockRequest);

        assertNotNull(result);
        // Should handle null gracefully
        assertTrue(result.contains("Testnull"));
    }

    // ==================== getResponse Tests ====================

    @Test
    void testGetResponse_Success() throws IOException {
        String jsonResponse = """
            {
              "response": "This is the Ollama response"
            }
            """;

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        String result = adapter.getResponse(mockResponse);

        assertEquals("This is the Ollama response", result);
        verify(mockResponse, times(2)).body();
    }

    @Test
    void testGetResponse_WithMultilineText() throws IOException {
        String jsonResponse = """
            {
              "response": "Line 1\\nLine 2\\nLine 3"
            }
            """;

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        String result = adapter.getResponse(mockResponse);

        assertNotNull(result);
        assertTrue(result.contains("Line 1"));
    }

    @Test
    void testGetResponse_EmptyResponse() throws IOException {
        String jsonResponse = """
            {
              "response": ""
            }
            """;

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        String result = adapter.getResponse(mockResponse);

        assertEquals("", result);
    }

    @Test
    void testGetResponse_NoResponseField() throws IOException {
        String jsonResponse = """
            {
              "notResponse": "Wrong field"
            }
            """;

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        IOException exception = assertThrows(IOException.class, () -> {
            adapter.getResponse(mockResponse);
        });

        assertTrue(exception.getMessage().contains("Invalid Ollama response"));
    }

    @Test
    void testGetResponse_ResponseFieldIsNull() throws IOException {
        String jsonResponse = """
            {
              "response": null
            }
            """;

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        IOException exception = assertThrows(IOException.class, () -> {
            adapter.getResponse(mockResponse);
        });

        assertTrue(exception.getMessage().contains("Invalid Ollama response"));
    }

    @Test
    void testGetResponse_ResponseFieldIsNotTextual() throws IOException {
        String jsonResponse = """
            {
              "response": 12345
            }
            """;

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        IOException exception = assertThrows(IOException.class, () -> {
            adapter.getResponse(mockResponse);
        });

        assertTrue(exception.getMessage().contains("Invalid Ollama response"));
    }

    @Test
    void testGetResponse_InvalidJson() throws IOException {
        String jsonResponse = "{ invalid json }";

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        assertThrows(IOException.class, () -> {
            adapter.getResponse(mockResponse);
        });
    }

    @Test
    void testGetResponse_EmptyJson() throws IOException {
        String jsonResponse = "{}";

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        IOException exception = assertThrows(IOException.class, () -> {
            adapter.getResponse(mockResponse);
        });

        assertTrue(exception.getMessage().contains("Invalid Ollama response"));
    }

    @Test
    void testGetResponse_ResponseFieldIsObject() throws IOException {
        String jsonResponse = """
            {
              "response": {
                "text": "Should be string not object"
              }
            }
            """;

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        IOException exception = assertThrows(IOException.class, () -> {
            adapter.getResponse(mockResponse);
        });

        assertTrue(exception.getMessage().contains("Invalid Ollama response"));
    }
}