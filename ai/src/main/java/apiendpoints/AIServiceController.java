/**
 * Author : Abhirami R Iyer
 */
package apiendpoints;

import aiservice.LlmService;
import com.fasterxml.jackson.databind.JsonNode;
import data.WhiteBoardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import request.AIDescriptionRequest;
import request.AIRegularisationRequest;
import request.AIRequestable;
import response.AIResponse;
import request.AiInsightsRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * REST controller for AI-based image processing services.
 * Handles interpretation and regularisation requests.
 */
@RestController
@RequestMapping("/api")
public class AIServiceController {

    /** Cloud-based AI service interface. */
    @Autowired
    private LlmService cloudService;

    /**
     * Interprets an uploaded image and generates a textual description.
     *
     * @param file uploaded image file (from client)
     * @return textual description of the image
     */
    @PostMapping("/image/interpret")
    public ResponseEntity<String> describe(
            @RequestParam("file") final MultipartFile file) {
        Path tempFile = null;
        try {
            // Save uploaded file temporarily
            tempFile = Files.createTempFile(
                    "upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            // Pass file path to your existing data class
            WhiteBoardData data = new WhiteBoardData(tempFile.toString());
            AIRequestable request = new AIDescriptionRequest(data);
            AIResponse response = cloudService.runProcess(request);

            return ResponseEntity.ok(response.getResponse());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        } finally {
            // Cleanup temporary file after processing
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Regularises a set of input points to produce a smoother output.
     *
     * @param points JSON string containing the points data
     * @return regularised point data as a response
     */
    @PostMapping("/image/regularise")
    public ResponseEntity<String> regularise(final @RequestBody String points) {
        try {
            AIRequestable request = new AIRegularisationRequest(points);
            AIResponse response = cloudService.runProcess(request);
            return ResponseEntity.ok(response.getResponse());
        } catch (IOException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).body(
                            "Error: " + e.getMessage());
        }
    }
    /**
     * API for sentiment analysis.
     * Recieves chats as a json file, does sentiment analysis,
     * and generates insights graph
     * @param chatData JSON object containing the chat data
     * @return a list float values to plot in the sentiment graph.
     */
    @PostMapping("/chat/sentiment")
    public ResponseEntity<String> sentiment(
            final @RequestBody JsonNode chatData) {
        try {
            AIRequestable request = new AiInsightsRequest(chatData);
            AIResponse response = cloudService.runProcess(request);
            return ResponseEntity.ok(response.getResponse());
        } catch (IOException e) {
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "Error: " + e.getMessage());
        }
    }
}
