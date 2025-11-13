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
import request.AIDescriptionRequest;
import response.AIResponse;
import response.InterpreterResponse;

import java.io.FileInputStream;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = main.Main.class)
@AutoConfigureMockMvc
public class cloudInterpreterTest {

    @Autowired MockMvc mockMvc;

    @MockBean
    private LlmService cloudService;

    @Test
    void testInterpreter() throws Exception {
        AIResponse mockResponse = new InterpreterResponse();
        mockResponse.setResponse("Mocked image interpretation from Gemini");
        when(cloudService.runProcess(any(AIDescriptionRequest.class)))
                .thenReturn(mockResponse);

        String filePath = Paths.get("src", "test", "resources", "images", "test.png").toString();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                new FileInputStream(filePath)
        );

        mockMvc.perform(multipart("/api/image/interpret")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Mocked image interpretation from Gemini"));

    }
}
