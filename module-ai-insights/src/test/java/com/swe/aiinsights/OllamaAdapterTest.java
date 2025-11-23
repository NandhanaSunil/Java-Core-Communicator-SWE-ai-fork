/*
 * -----------------------------------------------------------------------------
 *  File: OllamaAdapter.java
 *  Owner: Abhirami R Iyer
 *  Roll Number : 112201001
 *  Module : com.swe.aiinsights.data
 * -----------------------------------------------------------------------------
 */

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


    @Test
    void testBuildRequestWithTextData() throws JsonProcessingException {
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
    void testBuildRequestWithImageData() throws JsonProcessingException {
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
    void testBuildRequestContainsCorrectOptions() throws JsonProcessingException {
        when(mockRequest.getPrompt()).thenReturn("Test");
        when(mockRequest.getTextData()).thenReturn("Data");
        when(mockRequest.getImgData()).thenReturn(null);

        String result = adapter.buildRequest(mockRequest);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result);

        // Verify options
        assertTrue(jsonNode.has("options"));
        JsonNode options = jsonNode.get("options");

        assertTrue(result.contains("prompt"));
        assertFalse(jsonNode.get("stream").asBoolean());
        assertEquals("gemma3", jsonNode.get("model").asText());
        assertEquals(16384, options.get("num_ctx").asInt());
        assertEquals(0.2, options.get("temperature").asDouble(), 0.001);
        assertEquals(0.9, options.get("top_p").asDouble(), 0.001);
    }


    @Test
    void testGetResponseSuccess() throws IOException {
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
    void testGetResponseResponseFieldIsNotTextual() throws IOException {
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
    void testGetResponseInvalidJson() throws IOException {
        String jsonResponse = "{ invalid json }";

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.charStream()).thenReturn(new StringReader(jsonResponse));

        assertThrows(IOException.class, () -> {
            adapter.getResponse(mockResponse);
        });
    }

}