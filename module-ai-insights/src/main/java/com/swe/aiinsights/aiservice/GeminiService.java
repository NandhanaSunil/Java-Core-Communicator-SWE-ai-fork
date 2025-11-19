/**
 * Author : Abhirami R Iyer
 * Edited by : Nandhana Sunil
 *             Berelli Gouthami
 *
 * <p>
 * References
 *      1. https://ai.google.dev/gemini-api/docs/rate-limits
 * </p>
 */
package com.swe.aiinsights.aiservice;

import com.swe.aiinsights.generaliser.RequestGeneraliser;
import com.swe.aiinsights.modeladapter.GeminiAdapter;
import com.swe.aiinsights.modeladapter.ModelAdapter;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import com.swe.aiinsights.response.AiResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.swe.aiinsights.customexceptions.RateLimitException;

// import com.swe.cloud.datastructures.TimeRange;
// import com.swe.cloud.datastructures.Entity;
// import com.swe.cloud.functionlibrary.CloudFunctionLibrary;
// import com.swe.cloud.datastructures.CloudResponse;

/**
 * Gemini Service builds the request and calls the AI api.
 * Receives the AI response.
 */
public final class GeminiService implements LlmService {
    /**
     * Loads environment variables from the .env file.
     */
    private static Dotenv dotenv = Dotenv.load();
    /**
     * gets the GEMINI_URL_TEMPLATE from the .env file.
     */
    private static final String GEMINI_API_URL_TEMPLATE =
            dotenv.get("GEMINI_URL");
    /**
     * Sets the Media type used for JSON requests.
     */
    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");
    /**
     * The Gemini API key used for request authentication.
     */
    private final String geminiApiKey;
    /**
     * http client for the requests.
     */
    private final OkHttpClient httpClient;

    /**
     * Constructor for initialising the http client for making the request.
     */
    public GeminiService() {
        //fetched the api key from the
        // env file (to be changed to fetch from cloud)
        /** cloud functions to get key
         CloudFunctionLibrary cloud = new CloudFunctionLibrary();
         Entity req = new Entity("AI_INSIGHT", "credentials", "gemini", "key", -1, new TimeRange(0, 0),null);
         // Response response =testCloudFunctionLibrary.cloudPost(testEntity)
         String key_from_cloud;
         cloud.cloudGet(req).thenAccept(response -> {
         // Object cleanedData = response.data;   // <- NO getData()
         key_from_cloud = response.data();
         });*/
        this.geminiApiKey = dotenv.get("GEMINI_API_KEY"); //change this in production

        final int timeout = 200;
        final int readMul = 6;
        // creating an http client
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout * readMul, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AiResponse runProcess(final RequestGeneraliser aiRequest)
            throws IOException {

        AiResponse returnResponse = aiRequest.getAiResponse();

        ModelAdapter adapter = new GeminiAdapter();

        String requestBody = adapter.buildRequest(aiRequest);

        System.out.println("DEBUG >>> RequestString: Recieved from adapter");

        final String apiUrl =
                GEMINI_API_URL_TEMPLATE + geminiApiKey;

        // the request body is created using the json string.
        final RequestBody body =
                RequestBody.create(requestBody, JSON);

        // the post request is created.
        final Request request =
                new Request.Builder()
                        .url(apiUrl)
                        .post(body)
                        .build();

        // http post
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 429) {
                    // This is the trigger for the failover
                    //     System.out.println("Rate limit is hit !!!!!");
                    throw new RateLimitException("Gemini API rate limit hit. Status code 429.");
                }

                throw new
                        IOException("Unexpected code"
                        + response + " - " + response.body().string());
            }

            String textResponse = adapter.getResponse(response);
            returnResponse.setResponse(textResponse);

            return returnResponse;
        }


    }
}