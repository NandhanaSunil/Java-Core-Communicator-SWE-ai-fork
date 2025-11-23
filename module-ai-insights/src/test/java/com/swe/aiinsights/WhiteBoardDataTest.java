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

    // ==================== Constructor Tests ====================

    @Test
    void testConstructor_ValidImageFile() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("test-image.png");
        byte[] imageData = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}; // PNG header
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
    void testConstructor_NonExistentFile() {
        // Arrange
        String nonExistentPath = "/nonexistent/path/image.png";

        // Act & Assert
        assertThrows(IOException.class, () -> {
            new WhiteBoardData(nonExistentPath);
        });
    }

    @Test
    void testConstructor_EmptyFile() throws IOException {
        // Arrange
        Path emptyFile = tempDir.resolve("empty.png");
        Files.write(emptyFile, new byte[0]);

        // Act
        WhiteBoardData wbData = new WhiteBoardData(emptyFile.toString());

        // Assert
        assertNotNull(wbData);
        assertEquals("", wbData.getContent()); // Empty file = empty Base64
    }

    @Test
    void testConstructor_LargeImageFile() throws IOException {
        // Arrange
        Path largeFile = tempDir.resolve("large-image.png");
        byte[] largeData = new byte[10000]; // 10KB
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }
        Files.write(largeFile, largeData);

        // Act
        WhiteBoardData wbData = new WhiteBoardData(largeFile.toString());

        // Assert
        assertNotNull(wbData);
        String expectedBase64 = Base64.getEncoder().encodeToString(largeData);
        assertEquals(expectedBase64, wbData.getContent());
    }

    @Test
    void testConstructor_JPEGFile() throws IOException {
        // Arrange
        Path jpegFile = tempDir.resolve("test-image.jpg");
        byte[] jpegData = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPEG header
        Files.write(jpegFile, jpegData);

        // Act
        WhiteBoardData wbData = new WhiteBoardData(jpegFile.toString());

        // Assert
        assertNotNull(wbData);
        String expectedBase64 = Base64.getEncoder().encodeToString(jpegData);
        assertEquals(expectedBase64, wbData.getContent());
    }

    @Test
    void testConstructor_WithSpecialCharactersInPath() throws IOException {
        // Arrange
        Path fileWithSpecialChars = tempDir.resolve("test image with spaces.png");
        byte[] data = new byte[]{0x01, 0x02, 0x03};
        Files.write(fileWithSpecialChars, data);

        // Act
        WhiteBoardData wbData = new WhiteBoardData(fileWithSpecialChars.toString());

        // Assert
        assertNotNull(wbData);
        assertNotNull(wbData.getContent());
    }

    @Test
    void testConstructor_RelativePath() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("relative-test.png");
        byte[] data = new byte[]{0x10, 0x20, 0x30};
        Files.write(imageFile, data);

        // Act - Using absolute path (relative paths might cause issues)
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toAbsolutePath().toString());

        // Assert
        assertNotNull(wbData);
        String expectedBase64 = Base64.getEncoder().encodeToString(data);
        assertEquals(expectedBase64, wbData.getContent());
    }

    // ==================== getContent() Tests ====================

    @Test
    void testGetContent_AfterConstruction() throws IOException {
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
    void testGetContent_MultipleCallsReturnSameValue() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("multi-call.png");
        byte[] imageData = new byte[]{0x01, 0x02};
        Files.write(imageFile, imageData);
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        // Act
        String content1 = wbData.getContent();
        String content2 = wbData.getContent();
        String content3 = wbData.getContent();

        // Assert
        assertEquals(content1, content2);
        assertEquals(content2, content3);
    }

    @Test
    void testGetContent_EmptyImage() throws IOException {
        // Arrange
        Path emptyFile = tempDir.resolve("empty-content.png");
        Files.write(emptyFile, new byte[0]);
        WhiteBoardData wbData = new WhiteBoardData(emptyFile.toString());

        // Act
        String content = wbData.getContent();

        // Assert
        assertEquals("", content);
    }

    // ==================== setContent() Tests ====================

    @Test
    void testSetContent_OverridesOriginalContent() throws IOException {
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

    @Test
    void testSetContent_WithNull() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("null-content.png");
        Files.write(imageFile, new byte[]{0x01});
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        // Act
        wbData.setContent(null);

        // Assert
        assertNull(wbData.getContent());
    }

    @Test
    void testSetContent_WithEmptyString() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("empty-string.png");
        Files.write(imageFile, new byte[]{0x01});
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        // Act
        wbData.setContent("");

        // Assert
        assertEquals("", wbData.getContent());
    }

    @Test
    void testSetContent_MultipleTimes() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("multi-set.png");
        Files.write(imageFile, new byte[]{0x01});
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        // Act
        wbData.setContent("First");
        assertEquals("First", wbData.getContent());

        wbData.setContent("Second");
        assertEquals("Second", wbData.getContent());

        wbData.setContent("Third");
        assertEquals("Third", wbData.getContent());
    }

    @Test
    void testSetContent_WithValidBase64() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("valid-base64.png");
        Files.write(imageFile, new byte[]{0x01});
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        byte[] newData = new byte[]{0x10, 0x20, 0x30};
        String validBase64 = Base64.getEncoder().encodeToString(newData);

        // Act
        wbData.setContent(validBase64);

        // Assert
        assertEquals(validBase64, wbData.getContent());

        // Verify we can decode it back
        byte[] decoded = Base64.getDecoder().decode(wbData.getContent());
        assertArrayEquals(newData, decoded);
    }

    @Test
    void testSetContent_WithLongBase64String() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("long-base64.png");
        Files.write(imageFile, new byte[]{0x01});
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        byte[] largeData = new byte[5000];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) i;
        }
        String longBase64 = Base64.getEncoder().encodeToString(largeData);

        // Act
        wbData.setContent(longBase64);

        // Assert
        assertEquals(longBase64, wbData.getContent());
    }

    @Test
    void testSetContent_WithSpecialCharacters() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("special-chars.png");
        Files.write(imageFile, new byte[]{0x01});
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());

        String specialContent = "Base64+/==Content123";

        // Act
        wbData.setContent(specialContent);

        // Assert
        assertEquals(specialContent, wbData.getContent());
    }

    // ==================== Integration Tests ====================

    @Test
    void testCompleteWorkflow_ConstructSetGet() throws IOException {
        // Arrange
        Path imageFile = tempDir.resolve("workflow.png");
        byte[] originalData = new byte[]{0x11, 0x22, 0x33};
        Files.write(imageFile, originalData);

        // Act - Construct
        WhiteBoardData wbData = new WhiteBoardData(imageFile.toString());
        String originalContent = wbData.getContent();

        // Act - Set new content
        String newContent = "UpdatedContent";
        wbData.setContent(newContent);
        String updatedContent = wbData.getContent();

        // Assert
        assertNotEquals(originalContent, updatedContent);
        assertEquals(newContent, updatedContent);
        assertEquals(Base64.getEncoder().encodeToString(originalData), originalContent);
    }

    @Test
    void testBase64Encoding_BinaryData() throws IOException {
        // Arrange - Create file with binary data
        Path binaryFile = tempDir.resolve("binary.png");
        byte[] binaryData = new byte[256];
        for (int i = 0; i < 256; i++) {
            binaryData[i] = (byte) i;
        }
        Files.write(binaryFile, binaryData);

        // Act
        WhiteBoardData wbData = new WhiteBoardData(binaryFile.toString());
        String base64Content = wbData.getContent();

        // Assert - Verify encoding/decoding roundtrip
        byte[] decoded = Base64.getDecoder().decode(base64Content);
        assertArrayEquals(binaryData, decoded);
    }

    @Test
    void testConstructor_ReadOnlyFile() throws IOException {
        // Arrange
        Path readOnlyFile = tempDir.resolve("readonly.png");
        Files.write(readOnlyFile, new byte[]{0x01, 0x02});
        readOnlyFile.toFile().setReadOnly();

        try {
            // Act - Should still be able to read
            WhiteBoardData wbData = new WhiteBoardData(readOnlyFile.toString());

            // Assert
            assertNotNull(wbData);
            assertNotNull(wbData.getContent());
        } finally {
            // Cleanup - restore write permission
            readOnlyFile.toFile().setWritable(true);
        }
    }
}