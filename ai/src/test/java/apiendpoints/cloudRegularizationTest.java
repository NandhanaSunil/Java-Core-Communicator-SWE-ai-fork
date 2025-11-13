package apiendpoints;

import aiservice.LlmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import request.AIRegularisationRequest;
import response.AIResponse;
import response.RegulariserResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = main.Main.class)
@AutoConfigureMockMvc
public class cloudRegularizationTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LlmService cloudService;

    @Test
    void testRegularizer() throws Exception {
        AIResponse mockResponse = new RegulariserResponse();
        mockResponse.setResponse("Mocked Regularised data from Gemini");
        when(cloudService.runProcess(any(AIRegularisationRequest.class)))
                .thenReturn(mockResponse);


        mockMvc.perform(post("/api/image/regularise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\": [[1,2],[3,4]]}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mocked Regularised data from Gemini"));

    }
}
