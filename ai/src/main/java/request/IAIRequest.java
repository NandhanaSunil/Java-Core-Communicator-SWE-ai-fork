package request;

/**
 * Defines an interface for making types of requests to AI.
 */
public interface IAIRequest {

    /**
     * Returns the prompt for the particular AI request.
     *
     * @return the context as a String
     */
    String getContext();

    /**
     * Returns the input data for the request.
     *
     * @return the input as a String
     */
    String getInput();

    /**
     * Returns the type of request.
     *
     * @return "REG" if regularization,
     * "DESC" if interpretation
     */
    String getReqType();
}
