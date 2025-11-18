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
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.swe.aiinsights.customexceptions.RateLimitException;

// Acts as an orchestrator to provide an LLM Service
public class LlmOrchestratorService implements LlmService {

    private final List<LlmService> llmServices; // List of all LLMs in order of preference
    private volatile int activeServiceIndex = 0; // Index of the currently used service

    /**
     * Constructor accepts a list of LlmService instances in the desired failover order.
     */
    public LlmOrchestratorService(List<LlmService> services) {
        if (services == null || services.isEmpty()) {
            throw new IllegalArgumentException("LLM service list cannot be empty.");
        }
        this.llmServices = services;
    }

    /**
     * Tries the active service. If it fails due to a RateLimitException,
     * it switches to the next service in the list and retries the request.
     */
    @Override
    public AiResponse runProcess(final AiRequestable request) throws IOException {
        
        // Start the attempt from the currently active service index
        int startingIndex = activeServiceIndex;
        
        for (int i = startingIndex; i < llmServices.size(); i++) {
            LlmService currentService = llmServices.get(i);
            String serviceName = currentService.getClass().getSimpleName();

            System.out.println("INFO: Attempting service: " + serviceName + " for request: " + request.getReqType());
            
            try {
                // Try to process the request with the current service
                AiResponse response = currentService.runProcess(request);
                
                // If successful, update the active index if a switch occurred
                if (i != startingIndex) {
                    // Switch was successful, permanently use this new service
                    activeServiceIndex = i; 
                    System.out.println("SUCCESS: Permanently switched to service: " + serviceName);
                }
                return response;

            } catch (RateLimitException e) {
                // This service is rate-limited, move to the next service in the chain
                System.err.println("WARNING: Service " + serviceName + " failed due to rate limit. Attempting next service.");
                
                if (i == llmServices.size() - 1) {
                    // If this was the last service in the chain, throw the exception
                    System.err.println("FATAL: All services in the chain failed.");
                    throw e; 
                }
                
                // Continue to the next iteration (the next LLM)
            }
        }
        
        // Should be unreachable if the last service throws a final exception,
        // but included for safety against possible non-RateLimit IOExceptions in the last service.
        throw new IOException("All configured LLM services failed to process the request.");
    }
}