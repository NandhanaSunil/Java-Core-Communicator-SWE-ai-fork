package com.swe.aiinsights;

import com.swe.aiinsights.data.WhiteBoardData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WhiteBoardData with 100% code coverage.
 */
class WhiteBoardDataTest {

    @TempDir
    Path tempDir;


    @Test
    void testConstructorValidImageFile() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("test-image.png");
        byte[] imageData = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};
        Files.write(imageFile, imageData);

        // Act
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        // Assert
        assertNotNull(wbData);
        assertNotNull(wbData.getContent());

        // Verify Base64 encoding
        String expectedBase64 = Base64.getEncoder().encodeToString(imageData);
        assertEquals(expectedBase64, wbData.getContent());
    }

    @Test
    void testConstructorNonExistentFile() {
        // Arrange
        String nonExistentPath = "/nonexistent/path/image.png";

        // Act & Assert
        assertThrows(IOException.class, () -> {
            new WhiteBoardData(nonExistentPath);
        });
    }


    @Test
    void testGetContent() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("content-test.png");
        byte[] imageData = new byte[]{0x41, 0x42, 0x43}; // ABC
        Files.write(imageFile, imageData);
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        // Act
        String content = wbData.getContent();

        // Assert
        assertNotNull(content);
        String expectedBase64 = Base64.getEncoder().encodeToString(imageData);
        assertEquals(expectedBase64, content);
    }

    @Test
    void testSetContent() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("override-test.png");
        byte[] originalData = new byte[]{0x01, 0x02};
        Files.write(imageFile, originalData);
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        String newContent = "NewBase64EncodedContent";

        // Act
        wbData.setContent(newContent);

        // Assert
        assertEquals(newContent, wbData.getContent());

        // Verify original content is replaced
        String originalBase64 = Base64.getEncoder().encodeToString(originalData);
        assertNotEquals(originalBase64, wbData.getContent());
    }



}