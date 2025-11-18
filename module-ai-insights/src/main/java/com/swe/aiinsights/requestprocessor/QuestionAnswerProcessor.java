package com.swe.aiinsights.requestprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.swe.aiinsights.request.AiRequestable;

/**
 * Handles the creation of the JSON body for Question & Answer requests.
 * It builds a prompt that instructs the LLM on classification and conditional answering.
 */
public class QuestionAnswerProcessor implements RequestProcessor {

    @Override
    public String processRequest(final ObjectMapper objectMapper,
                                 final AiRequestable aiRequest) {
        try {
            // The core conditional logic prompt for the LLM
            final String prompt =
                    "You are a strict, rule-based Q&A system. STRICTLY FOLLOW THESE RULES:\n"
                            + "1. **CLASSIFICATION**: Analyze the 'USER_QUESTION'. Determine if it is 'CONTEXTUAL' (requires the summary) or 'GENERIC' (can be answered with general knowledge).\n"
                            + "2. **ANSWERING RULE**: \n"
                            + "   - If classified as **CONTEXTUAL**, you MUST **SEARCH THE ENTIRE 'ACCUMULATED_SUMMARY'** provided between the dashes (---) to find the answer. \n"
                            + "     - If you find the answer, provide it directly, quoting or paraphrasing from the summary. \n"
                            + "     - ONLY if the answer is unequivocally absent after searching, state: 'The information is missing from the context.'\n"
                            + "   - If classified as **GENERIC**, you MUST answer based on your general knowledge and **ignore** the summary.\n"
                            + "3. **FORMATTING**: Begin your response with 'Classification: [CONTEXTUAL/GENERIC]' followed by the answer.\n\n"
                            + aiRequest.getInput();

            // Build the JSON structure for the API call
            ObjectNode root = objectMapper.createObjectNode();
            ObjectNode content = objectMapper.createObjectNode();
            ObjectNode part = objectMapper.createObjectNode();

            part.put("text", prompt);
            content.set("parts", objectMapper.createArrayNode().add(part));
            root.set("contents", objectMapper.createArrayNode().add(content));

            return objectMapper.writeValueAsString(root);

        } catch (Exception e) {
            throw new RuntimeException("Error building Question & Answer request JSON", e);
        }
    }
}