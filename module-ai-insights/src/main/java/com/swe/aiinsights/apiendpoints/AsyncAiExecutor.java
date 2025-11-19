/**
 * Author : Abhirami R Iyer
 */
package com.swe.aiinsights.apiendpoints;

import com.swe.aiinsights.aiservice.GeminiService;
import com.swe.aiinsights.aiservice.LlmService;
import com.swe.aiinsights.aiservice.OllamaService;
import com.swe.aiinsights.configu.AsyncConfig;
import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.request.AiRequestable;
import com.swe.aiinsights.response.AiResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class AsyncAiExecutor {

    private final LlmService llmService = new GeminiService();
    private static final Executor aiExecutor = AsyncConfig.aiExecutor();



    public CompletableFuture<String> execute(final AiRequestable req) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        System.out.println(">>> DEBUG : Creating RequestGeneralised...");
                        RequestGeneraliser general = new RequestGeneraliser(req);

                        System.out.println(">>> DEBUG : Calling llmService.runProcess()...");
                        AiResponse aiResponse = llmService.runProcess(general);

                        System.out.println(">>> DEBUG : Received response");
                        return aiResponse.getResponse();

                    }  catch (IOException e) {
                        System.err.println(">>> DEBUG :  IOException in execute: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        System.err.println(">>> DEBUG : Unexpected exception: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                }, aiExecutor);
    }

}
