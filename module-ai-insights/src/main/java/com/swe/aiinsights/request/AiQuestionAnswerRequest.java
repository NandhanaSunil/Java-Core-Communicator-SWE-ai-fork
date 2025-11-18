package com.swe.aiinsights.request;

/**
 * Creates a new Question & Answer request.
 * This request includes the user's question and the accumulated chat summary context.
 */
public class AiQuestionAnswerRequest implements AiRequestable<String> {

    private final String question;
    private final String accumulatedSummary;

    public AiQuestionAnswerRequest(final String question, final String accumulatedSummary) {
        this.question = question;
        this.accumulatedSummary = accumulatedSummary;
    }

    @Override
    public String getContext() {
        // Simple context for the AI service runner
        return "Answer the user's question. First, classify if the question is 'CONTEXTUAL' or 'GENERIC'.";
    }

    /**
     * Combines the summary and the question for the LLM input payload.
     */
    @Override
    public String getInput() {
        // Send the raw accumulated summary with the new question.
        return "ACCUMULATED_CONTEXT:\n" + accumulatedSummary + "\n\nUSER_QUESTION: " + question;
    }

    @Override
    public String getReqType() {
        return "QA";
    }
}