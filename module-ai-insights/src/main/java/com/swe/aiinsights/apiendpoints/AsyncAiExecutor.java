/**
 * Author : Abhirami R Iyer
 */
package apiendpoints;

import aiservice.GeminiService;
import aiservice.LlmService;
import aiservice.OllamaService;
import configu.AsyncConfig;
import request.AiRequestable;
import response.AiResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class AsyncAiExecutor {


    private LlmService llmService = new OllamaService();


    private Executor aiExecutor = new AsyncConfig().aiExecutor();


    public CompletableFuture<String> execute(AiRequestable req) {

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        AiResponse aiResponse = llmService.runProcess(req);
                        return aiResponse.getResponse();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }, aiExecutor);
    }

}
