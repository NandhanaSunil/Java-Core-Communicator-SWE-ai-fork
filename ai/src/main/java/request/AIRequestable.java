package request;

/**
 * Defines an interface for making types of requests to AI.
 *
 * @param <T> the type of the output of getInput()
 */
public interface AIRequestable<T> {

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
    T getInput();

    /**
     * Returns the type of request.
     *
     * @return "REG" if regularization,
     * "DESC" if interpretation
     * "INS" if insights generation
     */
    String getReqType();
}
