package request;

/**
 * Creates a new summarisation request.
 *
 * @param inputJson the JSON string containing chat data
 */
public class AISummarisationRequest implements IAIRequest<String> {

    /**
     * Raw chat data in JSON format.
     */
    private final String jsonInput;

    /**
     * Creates a summarisation request.
     *
     * @param inputJson the chat data JSON
     */
    public AISummarisationRequest(final String inputJson) {
        this.jsonInput = inputJson;
    }

    /**
     * Provides a general instruction for the AI model.
     *
     * @return summarisation context prompt
     */
    @Override
    public String getContext() {
        return "Summarize the following chat data into a concise, meaningful summary.";
    }

    /**
     * Returns the chat input to be summarised.
     *
     * @return JSON-formatted chat string
     */
    @Override
    public String getInput() {
        return jsonInput;
    }

    /**
     * Specifies the type of AI request being made.
     *
     * @return request type identifier
     */
    @Override
    public String getReqType() {
        return "SUMMARISE";
    }
}
