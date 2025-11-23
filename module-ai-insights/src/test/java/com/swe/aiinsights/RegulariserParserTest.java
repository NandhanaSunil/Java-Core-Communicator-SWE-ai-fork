package com.swe.aiinsights;

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
        String inputJson = """
                  {
                  "ShapeId": "c585b84a",
                  "Type": "FREEHAND",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 2,
                  "CreatedBy": "user_default",
                  "LastModifiedBy": "user_default",
                  "IsDeleted": false
                }
                """;

        String aiResponse = """
               {
                  "ShapeId": "abcd",
                  "Type": "ELLIPSE",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    },
                    {
                      "X": 10,
                      "Y": 20
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 3,
                  "CreatedBy": "user1",
                  "LastModifiedBy": "user1",
                  "IsDeleted": false
                }
               """;

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        assertNotNull(result);
        JsonNode resultNode = objectMapper.readTree(result);

        assertEquals("c585b84a", resultNode.get("ShapeId").asText());
        assertEquals("ELLIPSE", resultNode.get("Type").asText());
        assertEquals("#FF000000", resultNode.get("Color").asText());
        assertEquals(2, resultNode.get("Thickness").asInt());
        assertEquals("user_default", resultNode.get("CreatedBy").asText());
        assertEquals("user_default", resultNode.get("LastModifiedBy").asText());
        assertFalse(resultNode.get("IsDeleted").asBoolean());

        // Verify points (should only have first 2)
        JsonNode points = resultNode.get("Points");
        assertEquals(2, points.size());
    }


    @Test
    void testParseInputValidResponse2() throws JsonProcessingException {
        // Arrange
        String inputJson = """
                  {
                  "ShapeId": "c585b84a",
                  "Type": "FREEHAND",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 2,
                  "CreatedBy": "user_default",
                  "LastModifiedBy": "user_default",
                  "IsDeleted": false
                }
                """;

        String aiResponse = """
               ```json{
                  "ShapeId": "abcd",
                  "type": "ELLIPSE",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    },
                    {
                      "X": 10,
                      "Y": 20
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 3,
                  "CreatedBy": "user1",
                  "LastModifiedBy": "user1",
                  "IsDeleted": false
                }```
               """;

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        assertNotNull(result);
        JsonNode resultNode = objectMapper.readTree(result);

        assertEquals("c585b84a", resultNode.get("ShapeId").asText());
        assertEquals("ELLIPSE", resultNode.get("Type").asText());
        assertEquals("#FF000000", resultNode.get("Color").asText());
        assertEquals(2, resultNode.get("Thickness").asInt());
        assertEquals("user_default", resultNode.get("CreatedBy").asText());
        assertEquals("user_default", resultNode.get("LastModifiedBy").asText());
        assertFalse(resultNode.get("IsDeleted").asBoolean());

        // Verify points (should only have first 2)
        JsonNode points = resultNode.get("Points");
        assertEquals(2, points.size());
    }


    @Test
    void testParseInputNoPoints() throws JsonProcessingException {
        // Arrange
        String inputJson = """
                  {
                  "ShapeId": "c585b84a",
                  "Type": "FREEHAND",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 2,
                  "CreatedBy": "user_default",
                  "LastModifiedBy": "user_default",
                  "IsDeleted": false
                }
                """;

        String aiResponse = """
               {
                  "ShapeId": "abcd",
                  "Type": "ELLIPSE",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 3,
                  "CreatedBy": "user1",
                  "LastModifiedBy": "user1",
                  "IsDeleted": false
                }
               """;

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert
        assertEquals(result, inputJson);
    }


    @Test
    void testParseInputNullAiResponse() throws JsonProcessingException {
        // Arrange
        String inputJson = """
                {
                  "ShapeId": "c585b84a-d56c-45b8-a0e1-827ae20a014a",
                  "Type": "FREEHAND",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 2,
                  "CreatedBy": "user_default",
                  "LastModifiedBy": "user_default",
                  "IsDeleted": false
                }
                """;

        // Act
        String result = parser.parseInput(inputJson, "ABCD");

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }


    @Test
    void testParseInputAiResponseWithoutPoints() throws JsonProcessingException {
        // Arrange
        String inputJson = """
                {
                  "ShapeId": "c585b84a-d56c-45b8-a0e1-827ae20a014a",
                  "Type": "FREEHAND",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 2,
                  "CreatedBy": "user_default",
                  "LastModifiedBy": "user_default",
                  "IsDeleted": false
                }
                """;

        String aiResponse = "{\"type\":\"Rectangle\"}";

        // Act
        String result = parser.parseInput(inputJson, aiResponse);

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }


    @Test
    void testParseInputInvalidJsonAiResponse() throws JsonProcessingException {
        // Arrange
        String inputJson = """
                {
                  "ShapeId": "c585b84a-d56c-45b8-a0e1-827ae20a014a",
                  "Type": "FREEHAND",
                  "Points": [
                    {
                      "X": 10,
                      "Y": 20
                    },
                    {
                      "X": 30,
                      "Y": 40
                    },
                    {
                      "X": 11,
                      "Y": 19
                    }
                  ],
                  "Color": "#FF000000",
                  "Thickness": 2,
                  "CreatedBy": "user_default",
                  "LastModifiedBy": "user_default",
                  "IsDeleted": false
                }
                """;

        String invalidAiResponse = "{invalid json}";

        // Act
        String result = parser.parseInput(inputJson, invalidAiResponse);

        // Assert - Should return input unchanged
        assertEquals(inputJson, result);
    }

}