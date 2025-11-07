/**
 * Author : Abhirami R Iyer
 */
package aiservice;

import request.AIRequestable;
import response.AIResponse;

import java.io.IOException;

/**
 * Interface for the llm services.
 */
public interface LlmService {
    /**
     * Function to send the request to AI api and get the response.
     * @param request holds the request of the user
     * @return IAIResponse, returns the response from AI
     * @throws IOException
     */
    AIResponse runProcess(AIRequestable request) throws IOException;

}
