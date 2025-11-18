package com.swe.aiinsights.response;

/**
 * Represents the AI's response to a Question & Answer request.
 */
public class QuestionAnswerResponse implements AiResponse {

    private String responseText;

    @Override
    public void setResponse(final String text) {
        this.responseText = text;
    }

    @Override
    public String getResponse() {
        return this.responseText;
    }
}