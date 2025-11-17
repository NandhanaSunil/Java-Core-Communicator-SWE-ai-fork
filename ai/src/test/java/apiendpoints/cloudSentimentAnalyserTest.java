package apiendpoints;

import aiservice.LlmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import request.AiInsightsRequest;
import response.AiResponse;
import response.InsightsResponse;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = main.Main.class)
@AutoConfigureMockMvc
public class cloudSentimentAnalyserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LlmService cloudService;

    @Test
    void testSentimentAnalyser() throws Exception {
        AiResponse mockResponse = new InsightsResponse();
        mockResponse.setResponse("Mocked Insights generation from Gemini");
        when(cloudService.runProcess(any(AiInsightsRequest.class)))
                .thenReturn(mockResponse);
        // Load JSON content from file
        String jsonPath = "src/test/resources/chatData/chat_data.json";
        String jsonContent = Files.readString(Paths.get(jsonPath));
        mockMvc.perform(post("/api/chat/sentiment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().string("Mocked Insights generation from Gemini"));

    }
}
