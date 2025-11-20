/**
 * Author : Abhirami R Iyer
 */
package com.swe.aiinsights.aiservice;

import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.modeladapter.ModelAdapter;
import com.swe.aiinsights.modeladapter.OllamaAdapter;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import com.swe.aiinsights.response.*;

import com.swe.aiinsights.response.SummariserResponse;
import com.swe.aiinsights.logging.CommonLogger;
import org.slf4j.Logger;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OllamaService implements LlmService {
    private static final Logger log = CommonLogger.getLogger(OllamaService.class);

    private static Dotenv dotenv = Dotenv.load();

    private static final String OLLAMA_URL = dotenv.get("OLLAMA_URL");
    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;


    public OllamaService() {
        final int timeout = 200;

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();
        log.info("OllamaService initialized with timeout: {} seconds", timeout);
    }

    @Override
    public AiResponse runProcess(final RequestGeneraliser aiRequest)
            throws IOException {

        AiResponse returnResponse = aiRequest.getAiResponse();

        ModelAdapter adapter = new OllamaAdapter();
        String jsonRequestBody = adapter.buildRequest(aiRequest);

//        System.out.println("DEBUG >>> RequestString: " + jsonRequestBody);
        log.debug("RequestString: {}", jsonRequestBody.substring(0, Math.min(200, jsonRequestBody.length())));

        // ---- Send request to Ollama ----
        RequestBody body = RequestBody.create(jsonRequestBody, JSON);
        Request request = new Request.Builder()
                .url(OLLAMA_URL)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            log.debug("Response code: {}", response.code());

            if (!response.isSuccessful()) {
                assert response.body() != null;
                String errorBody = response.body().string();
                log.error("Ollama API failed - Code: {}, Error: {}", response.code(), errorBody);
                throw new IOException("Unexpected code " + response
                        + " - " + response.body().string());
            }

            String textResponse = adapter.getResponse(response);

            returnResponse.setResponse(textResponse);
            log.info("Ollama API completed successfully");

            return returnResponse;
        }
    }
}
