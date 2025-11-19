/**
 * Author : Abhirami R Iyer
 *
 * <p>
 * The RegularizeParser post-processes the AI-generated regularisation
 * output, validates its structure, and merges it with the original
 * shape metadata. It ensures robust handling of improper or incomplete
 * AI responses, falling back to the original input when necessary.
 * </p>
 *
 * <p>
 * References:
 *     1. Internal Regularisation Specification (AI → Canvas)
 *     2. JSON Validation/Recovery Patterns — Jackson Databind
 * </p>
 */

package com.swe.aiinsights.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RegulariserParser {

    public String parseInput(final String inputJsonString, final String aiResponse) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode input = mapper.readTree(inputJsonString);

        String shapeId = input.get("ShapeId").asText();
        String color = input.get("Color").asText();
        int thickness = input.get("Thickness").asInt();
        String createdBy = input.get("CreatedBy").asText();
        String lastModifiedBy = input.get("LastModifiedBy").asText();
        boolean isDeleted = input.get("IsDeleted").asBoolean();

        String cleanedAI = aiResponse.trim();

        if (!cleanedAI.contains("{")) {
            return inputJsonString;
        }

        if (cleanedAI.startsWith("```")) {
            cleanedAI = cleanedAI.replace("```json", "")
                    .replace("```", "")
                    .trim();
        }


        JsonNode aiNode;
        try {
            aiNode = mapper.readTree(cleanedAI);

            if (!aiNode.isObject()) {
                return inputJsonString;
            }
        } catch (Exception e) {
            return inputJsonString;
        }

        if (!aiNode.has("Points")) {
            return inputJsonString;
        }

        String newType = aiNode.has("type")
                ? aiNode.get("type").asText()
                : aiNode.has("Type")
                ? aiNode.get("Type").asText()
                : input.path("Type").asText();

        ArrayNode aiPoints = (ArrayNode) aiNode.get("Points");

        if (aiPoints == null || aiPoints.isEmpty()) {
            return inputJsonString;
        }

        ArrayNode finalPoints = mapper.createArrayNode();

        if (aiPoints.size() >= 2) {
            // take first 2
            finalPoints.add(aiPoints.get(0));
            finalPoints.add(aiPoints.get(1));
        } else if (aiPoints.size() == 1) {
            // duplicate the single point
            finalPoints.add(aiPoints.get(0));
            finalPoints.add(aiPoints.get(0));
        } else {
            throw new IllegalArgumentException("AI returned zero points");
        }

        ObjectNode finalNode = mapper.createObjectNode();
        finalNode.put("ShapeId", shapeId);
        finalNode.put("Type", newType);
        finalNode.set("Points", finalPoints);
        finalNode.put("Color", color);
        finalNode.put("Thickness", thickness);
        finalNode.put("CreatedBy", createdBy);
        finalNode.put("LastModifiedBy", lastModifiedBy);
        finalNode.put("IsDeleted", isDeleted);

        return mapper.writeValueAsString(finalNode);





    }
}
