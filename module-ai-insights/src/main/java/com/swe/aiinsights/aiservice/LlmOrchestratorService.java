/**
 * Switches between cloud models and local LLMs when rate limit is hit.
 * 
 * <p>
 *     References :
 *         
 * </p>
 *
 * @author Nandhana Sunil
 * @version 1.0.0
 * @since 1.0.0
 */


package com.swe.aiinsights.aiservice;

import com.swe.aiinsights.actionitems.ActionItemsGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swe.aiinsights.imageinterpreter.ImageInterpreter;
import com.swe.aiinsights.insightsgenerator.InsightsGenerator;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import com.swe.aiinsights.regulariser.ImageRegularize;
import com.swe.aiinsights.request.AiRequestable;
import com.swe.aiinsights.requestprocessor.RequestProcessor;
import com.swe.aiinsights.response.ActionItemsResponse;
import com.swe.aiinsights.response.AiResponse;
import com.swe.aiinsights.response.InsightsResponse;
import com.swe.aiinsights.response.InterpreterResponse;
import com.swe.aiinsights.response.RegulariserResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.swe.aiinsights.requestprocessor.SummarisationProcessor;
import com.swe.aiinsights.response.SummariserResponse;
import com.swe.aiinsights.customexceptions.RateLimitException;

// Acts as an orchestrator to provide an LLM Service
public class LlmOrchestratorService implements LlmService {

    private final LlmService primaryService;
    private final LlmService fallbackService;
    private volatile boolean isFallbackActive = false; // Flag to manage the switch

    public LlmOrchestratorService(LlmService primary, LlmService fallback) {
        this.primaryService = primary;
        this.fallbackService = fallback;
    }

    /**
     * Tries the primary service (Clous models). If a RateLimitException is caught,
     * it switches to the fallback service (Ollama) and re-runs the request.
     */
    @Override
    public AiResponse runProcess(final AiRequestable request) throws IOException {
        
        // 1. Check if we've already switched to fallback
        if (isFallbackActive) {
            System.out.println("INFO: Using Fallback Service (Ollama) for request: " + request.getReqType());
            return fallbackService.runProcess(request);
        }

        // 2. Try the Primary Service (Gemini)
        try {
            System.out.println("INFO: Attempting Primary Service (Gemini) for request: " + request.getReqType());
            return primaryService.runProcess(request);
        } catch (RateLimitException e) {
            
            // 3. Rate limit hit - Log the event and activate the fallback
            System.err.println("WARNING: Primary Service (Gemini) failed due to rate limit. Switching to Fallback (Ollama).");
            isFallbackActive = true;
            
            // 4. Re-run the request immediately with the Fallback Service (Ollama)
            try {
                System.out.println("INFO: Retrying request with Fallback Service (Ollama).");
                return fallbackService.runProcess(request);
            } catch (IOException fallbackE) {
                // If the fallback also fails, throw a composite exception
                System.err.println("FATAL: Fallback Service (Ollama) also failed.");
                throw new IOException("Both Primary (Gemini) and Fallback (Ollama) services failed. Primary error: " 
                                      + e.getMessage() + ". Fallback error: " + fallbackE.getMessage(), fallbackE);
            }
        }
    }
}

