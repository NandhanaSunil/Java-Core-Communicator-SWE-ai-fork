/**
 * Author : Abhirami R Iyer
 *
 * <p>
 * The OllamaAdapter serialises generalised request data into the request
 * format expected by local Ollama models (e.g., Gemma3) and extracts the
 * plain-text response returned by the Ollama HTTP API.
 * </p>
 *
 * <p>
 * References:
 *     1. https://github.com/ollama/ollama/blob/main/docs/api.md
 *     2. https://www.ollama.com/library
 *     3. https://medium.com/@kapildevkhatik2/
 *       how-to-run-and-call-local-llms-with-ollama-a-developers-
 *          guide-to-building-offline-ai-apps-1161d9f3a0f8
 * </p>
*/


package com.swe.aiinsights.modeladapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swe.aiinsights.generaliser.RequestGeneraliser;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

public class OllamaAdapter implements ModelAdapter{

    @Override
    public String buildRequest(RequestGeneraliser request) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", "gemma3");
        root.put("prompt", request.getPrompt() + request.getTextData());
        ObjectNode options = objectMapper.createObjectNode();
        options.put("num_ctx", 16384);
        options.put("temperature", 0.2);
        options.put("top_p", 0.9);

        root.set("options", options);

        root.put("stream", false);

        String imgData = request.getImgData();
        if (imgData != null){
            ArrayNode images = root.putArray("images");
            images.add(request.getImgData());
        }

            return objectMapper.writeValueAsString(root);

    }

    @Override
    public String getResponse(Response response) throws IOException {
        assert response.body() != null;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson =
                objectMapper.readTree(response.body().charStream());

        JsonNode textNode = responseJson.get("response");

        if (textNode == null || !textNode.isTextual()) {
            throw new IOException("Invalid Ollama response: "
                    + responseJson.toPrettyString());
        }

//        System.out.println("DEBUG >>> ResponseString: Recieved");

        return textNode.asText();
    }
}
