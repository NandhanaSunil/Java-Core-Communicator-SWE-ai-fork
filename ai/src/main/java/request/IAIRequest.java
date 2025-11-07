package request;

/**
 * Represents a generic AI request.
 *
 * @param <T> the type of input data
 */
public interface IAIRequest<T> {

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
     */
    String getReqType();
}
