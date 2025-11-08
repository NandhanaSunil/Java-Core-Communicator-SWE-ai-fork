/**
 * Author : Abhirami R Iyer
 */
package response;

/**
 * Interface for implementing various types of responses.
 */
public interface AiResponse {

    /**
     * Returns the type of the AI response.
     *
     * @return the response type as a String
     */
    String getType();

    /**
     * Returns the content int the metadata of the AI response.
     *
     * @return the response content as a String
     */
    String getResponse();

    /**
     * Sets the content part of metadata corresponding to the AI response.
     *
     * @param content the response content to set, on receiving response from AI
     */
    void setResponse(String content);

}
