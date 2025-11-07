package aiservice;

import request.IAIRequest;
import response.IAIResponse;
import java.util.concurrent.CompletableFuture;

import java.io.IOException;

/**
 * Interface for the llm services.
 */
public interface ILLMService {
    /**
     * Function to send the request to AI api and get the response.
     * @param request holds the request of the user
     * @return IAIResponse, returns the response from AI
     * @throws IOException
     */
    IAIResponse runProcess(IAIRequest request) throws IOException;
    /**
     * Executes an AI request asynchronously.
     *
     * @param aiRequest the request to process
     * @return a future containing the AI response
     */
    CompletableFuture<IAIResponse> runProcessAsync(IAIRequest aiRequest);

}
