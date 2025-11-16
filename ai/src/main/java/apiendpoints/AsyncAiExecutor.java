package apiendpoints;

import aiservice.GeminiService;
import aiservice.LlmService;
import configu.AsyncConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import request.AiRequestable;
import response.AiResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class AsyncAiExecutor {


    private LlmService llmService = new GeminiService();


    @Qualifier("aiExecutor")
    private Executor aiExecutor = new AsyncConfig().aiExecutor();


    public CompletableFuture<ResponseEntity<String>> execute(AiRequestable req) {

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        AiResponse aiResponse = llmService.runProcess(req);
                        return ResponseEntity.ok(aiResponse.getResponse());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }, aiExecutor)
                .completeOnTimeout(
                        ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                                .body("AI processing timed out"),
                        100, TimeUnit.SECONDS
                );
    }

}
