/**
 * Author : Abhirami R Iyer
 */
package aiservice;

import request.AiRequestable;
import response.AiResponse;

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
    AiResponse runProcess(AiRequestable request) throws IOException;

}
