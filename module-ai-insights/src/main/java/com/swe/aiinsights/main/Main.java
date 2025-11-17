package com.swe.aiinsights.main;

import com.swe.aiinsights.apiendpoints.AiClientService;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


import java.io.IOException;

/**
 * Main class for running the Image Interpreter application.
 *
 * <p>This class loads the necessary classes,
 * prepares the request, and calls the Gemini API.
 * </p>
 */
public class Main {
    /**
     * Entry point of application.
     *
     * @param args  arguments of main
     * @throws IOException throws error if any of the implementation fails
     */
    public static void main(final String[] args) throws IOException, URISyntaxException {
        AiClientService service = new AiClientService();
        URL url = Main.class.getClassLoader().getResource("images/test.png");
        Path file = Paths.get(url.toURI());
        // CompletableFuture<String> resp = service.describe(file);

        ObjectMapper mapper = new ObjectMapper();

        JsonNode chat_data = mapper.readTree("""
                                {
  "messages": [
    {
      "from": "manager",
      "to": "team",
      "timestamp": "2025-11-12T09:00:00Z",
      "message": "Good morning everyone. Let's finalize the action plan for the feature rollout this Friday."
    },
    {
      "from": "developer1",
      "to": "manager",
      "timestamp": "2025-11-12T09:02:10Z",
      "message": "I'll handle the backend deployment scripts and make sure the new API endpoints are tested."
    },
    {
      "from": "developer2",
      "to": "manager",
      "timestamp": "2025-11-12T09:03:45Z",
      "message": "I'll update the UI for the feedback module and push the changes to the staging branch by tonight."
    },
    {
      "from": "qa_engineer",
      "to": "team",
      "timestamp": "2025-11-12T09:05:20Z",
      "message": "Once the staging branch is ready, I'll start the regression testing and prepare the report by Thursday evening."
    },
    {
      "from": "manager",
      "to": "team",
      "timestamp": "2025-11-12T09:06:55Z",
      "message": "Perfect. Decision: We'll do a final review meeting on Thursday at 5 PM before production deployment."
    },
    {
      "from": "developer1",
      "to": "qa_engineer",
      "timestamp": "2025-11-12T09:08:30Z",
      "message": "Please notify me once the regression tests start, so I can monitor API logs in real-time."
    },
    {
      "from": "developer2",
      "to": "team",
      "timestamp": "2025-11-12T09:10:00Z",
      "message": "Should we also update the documentation to reflect the new feedback endpoint?"
    },
    {
      "from": "manager",
      "to": "developer2",
      "timestamp": "2025-11-12T09:11:40Z",
      "message": "Yes, that's important. Action item: Update the API documentation and share it in the internal wiki by Thursday morning."
    },
    {
      "from": "qa_engineer",
      "to": "team",
      "timestamp": "2025-11-12T09:13:10Z",
      "message": "Noted. I'll also re-run smoke tests after deployment to verify user login and feedback submission workflows."
    },
    {
      "from": "manager",
      "to": "team",
      "timestamp": "2025-11-12T09:14:45Z",
      "message": "Summary of decisions: 1) Final review on Thursday 5 PM, 2) Documentation update due by Thursday morning, 3) Deployment on Friday morning."
    }
  ]
}

                                """);

        CompletableFuture<String> resp = service.action(chat_data);
        resp.thenAccept(System.out::println);

        
        // CompletableFuture<String> reg = service.regularise(points);
        // reg.thenAccept(System.out::println);
        // resp.thenAccept(System.out::println);
        System.out.println("AI Process - Running in another thread");


    }
}
