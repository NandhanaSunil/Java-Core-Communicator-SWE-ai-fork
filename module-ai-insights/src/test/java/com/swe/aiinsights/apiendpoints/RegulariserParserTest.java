package com.swe.aiinsights.apiendpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swe.aiinsights.parser.RegulariserParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RegulariserParser with 100% code coverage.
 */
class RegulariserParserTest {

    private RegulariserParser parser;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        parser = new RegulariserParser();
        objectMapper = new ObjectMapper();
    }

    // ==================== Valid Input Tests ====================

    @Test
    void testParseInput_ValidResponse() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape123\","
                + "\"Type\":\"FreeHand\","
                + "\"Points\":[[10,20],[30,40],[50,60]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":2,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user2\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Rectangle\","
                + "\"Points\":[[0,0],[100,100],[100,0],[0,100]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        assertNotNull(result);
        JsonNode resultNode = objectMapper.readTree(result);

        assertEquals("shape123", resultNode.get("ShapeId").asText());
        assertEquals("Rectangle", resultNode.get("Type").asText());
        assertEquals("red", resultNode.get("Color").asText());
        assertEquals(2, resultNode.get("Thickness").asInt());
        assertEquals("user1", resultNode.get("CreatedBy").asText());
        assertEquals("user2", resultNode.get("LastModifiedBy").asText());
        assertFalse(resultNode.get("IsDeleted").asBoolean());

        // Verify points (should only have first 2)
        JsonNode points = resultNode.get("Points");
        assertEquals(2, points.size());
    }

    @Test
    void testParseInput_TypeWithUppercaseT() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"blue\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"Type\":\"Ellipse\","
                + "\"Points\":[[0,0],[50,50],[100,100]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("Ellipse", resultNode.get("Type").asText());
    }

    @Test
    void testParseInput_TypeWithLowercaseT() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape2\","
                + "\"Type\":\"Line\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"green\","
                + "\"Thickness\":3,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Arrow\","
                + "\"Points\":[[0,0],[100,100]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("Arrow", resultNode.get("Type").asText());
    }

    // ==================== Fallback Tests - Missing/Invalid AI Response ====================

    @Test
    void testParseInput_NullAiResponse() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, null);

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }

    @Test
    void testParseInput_EmptyAiResponse() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, "");

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }

    @Test
    void testParseInput_AiResponseWithoutPoints() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{\"type\":\"Rectangle\"}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }

    @Test
    void testParseInput_AiResponseWithEmptyPoints() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{\"type\":\"Rectangle\",\"Points\":[]}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }

    @Test
    void testParseInput_InvalidJsonAiResponse() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String invalidAiResponse = "{invalid json}";

        // Act
        String result = parser.parseInput(inputJson, invalidAiResponse);

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }

    @Test
    void testParseInput_AiResponseWithoutBraces() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "This is plain text without JSON";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }

    // ==================== Markdown Fence Cleaning Tests ====================

    @Test
    void testParseInput_WithMarkdownJsonFence() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "```json\n{"
                + "\"type\":\"Square\","
                + "\"Points\":[[0,0],[50,50]}"
                + "\n```";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("Square", resultNode.get("Type").asText());
    }

    @Test
    void testParseInput_WithMarkdownFenceNoLanguage() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "```\n{"
                + "\"type\":\"Triangle\","
                + "\"Points\":[[0,0],[50,50],[25,75]]"
                + "}\n```";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("Triangle", resultNode.get("Type").asText());
    }

    @Test
    void testParseInput_WithWhitespaceAroundJson() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[10,20]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "   \n\n  {"
                + "\"type\":\"Pentagon\","
                + "\"Points\":[[0,0],[10,10],[20,20]]"
                + "}  \n\n  ";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("Pentagon", resultNode.get("Type").asText());
    }

    // ==================== Points Handling Tests ====================

    @Test
    void testParseInput_ExactlyTwoPoints() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Line\","
                + "\"Points\":[[0,0]],"
                + "\"Color\":\"black\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Line\","
                + "\"Points\":[[10,20],[30,40]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        JsonNode points = resultNode.get("Points");
        assertEquals(2, points.size());
        assertEquals(10, points.get(0).get(0).asInt());
        assertEquals(20, points.get(0).get(1).asInt());
        assertEquals(30, points.get(1).get(0).asInt());
        assertEquals(40, points.get(1).get(1).asInt());
    }

    @Test
    void testParseInput_MoreThanTwoPoints() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Polygon\","
                + "\"Points\":[[0,0]],"
                + "\"Color\":\"yellow\","
                + "\"Thickness\":2,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Polygon\","
                + "\"Points\":[[10,10],[20,20],[30,30],[40,40],[50,50]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should only keep first 2 points
        JsonNode resultNode = objectMapper.readTree(result);
        JsonNode points = resultNode.get("Points");
        assertEquals(2, points.size());
        assertEquals(10, points.get(0).get(0).asInt());
        assertEquals(20, points.get(1).get(1).asInt());
    }

    @Test
    void testParseInput_OnlyOnePoint() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Point\","
                + "\"Points\":[[0,0]],"
                + "\"Color\":\"purple\","
                + "\"Thickness\":5,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Point\","
                + "\"Points\":[[25,25]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should duplicate the single point
        JsonNode resultNode = objectMapper.readTree(result);
        JsonNode points = resultNode.get("Points");
        assertEquals(2, points.size());

        // Both points should be the same
        assertEquals(25, points.get(0).get(0).asInt());
        assertEquals(25, points.get(0).get(1).asInt());
        assertEquals(25, points.get(1).get(0).asInt());
        assertEquals(25, points.get(1).get(1).asInt());
    }

    // ==================== Type Fallback Tests ====================

    @Test
    void testParseInput_NoTypeInAiResponse() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"OriginalType\","
                + "\"Points\":[[0,0]],"
                + "\"Color\":\"cyan\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"Points\":[[10,10],[20,20]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should use original type
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("OriginalType", resultNode.get("Type").asText());
    }

    // ==================== Metadata Preservation Tests ====================

    @Test
    void testParseInput_PreservesAllMetadata() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"complex-shape-456\","
                + "\"Type\":\"FreeHand\","
                + "\"Points\":[[1,2]],"
                + "\"Color\":\"magenta\","
                + "\"Thickness\":7,"
                + "\"CreatedBy\":\"alice\","
                + "\"LastModifiedBy\":\"bob\","
                + "\"IsDeleted\":true"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Curve\","
                + "\"Points\":[[100,200],[300,400]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - All metadata should be preserved
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("complex-shape-456", resultNode.get("ShapeId").asText());
        assertEquals("Curve", resultNode.get("Type").asText());
        assertEquals("magenta", resultNode.get("Color").asText());
        assertEquals(7, resultNode.get("Thickness").asInt());
        assertEquals("alice", resultNode.get("CreatedBy").asText());
        assertEquals("bob", resultNode.get("LastModifiedBy").asText());
        assertTrue(resultNode.get("IsDeleted").asBoolean());
    }

    // ==================== Edge Cases ====================

    @Test
    void testParseInput_AiResponseIsArray() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Circle\","
                + "\"Points\":[[0,0]],"
                + "\"Color\":\"red\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "[{\"type\":\"Circle\",\"Points\":[[10,10]]}]";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should return input unchanged (not an object)
        assertEquals(inputJson, result);
    }

    @Test
    void testParseInput_ComplexNestedPoints() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Polygon\","
                + "\"Points\":[[0,0]],"
                + "\"Color\":\"orange\","
                + "\"Thickness\":3,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"ComplexPolygon\","
                + "\"Points\":[[10.5,20.7],[30.2,40.9],[50.1,60.3]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        assertEquals("ComplexPolygon", resultNode.get("Type").asText());
        JsonNode points = resultNode.get("Points");
        assertEquals(2, points.size());
    }

    @Test
    void testParseInput_NegativeCoordinates() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Line\","
                + "\"Points\":[[0,0]],"
                + "\"Color\":\"gray\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Line\","
                + "\"Points\":[[-10,-20],[-30,-40]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        JsonNode points = resultNode.get("Points");
        assertEquals(-10, points.get(0).get(0).asInt());
        assertEquals(-20, points.get(0).get(1).asInt());
    }

    @Test
    void testParseInput_ZeroCoordinates() throws JsonProcessingException {
        // Arrange
        String inputJson = "{"
                + "\"ShapeId\":\"shape1\","
                + "\"Type\":\"Point\","
                + "\"Points\":[[1,1]],"
                + "\"Color\":\"black\","
                + "\"Thickness\":1,"
                + "\"CreatedBy\":\"user1\","
                + "\"LastModifiedBy\":\"user1\","
                + "\"IsDeleted\":false"
                + "}";

        String aiResponse = "{"
                + "\"type\":\"Point\","
                + "\"Points\":[[0,0]]"
                + "}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        JsonNode resultNode = objectMapper.readTree(result);
        JsonNode points = resultNode.get("Points");
        assertEquals(0, points.get(0).get(0).asInt());
        assertEquals(0, points.get(0).get(1).asInt());
    }
}