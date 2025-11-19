package com.swe.aiinsights.generaliser;

import com.swe.aiinsights.request.AiRequestable;
import com.swe.aiinsights.response.*;

import java.util.ArrayList;
import java.util.List;

public class RequestGeneraliser {

    private String prompt;

    private String textData;

    private String imgData;

    private AiResponse aiResponse;

    private ArrayList<String> registeredKeys = new ArrayList<>(
            List.of("DESC", "REG", "INS", "SUMMARISE"));


    public RequestGeneraliser (AiRequestable request){

        System.out.println("DEBUG >>> ReqType: " + request.getReqType());
        System.out.println("DEBUG >>> Registered keys: " + registeredKeys);

        setPrompt(request.getContext());

        switch (request.getReqType()) {
            case "DESC" :
                setImgData(request.getInput().toString());
                aiResponse = new InterpreterResponse();
                break;
            case "REG":
                setTextData(request.getInput().toString());
                aiResponse = new RegulariserResponse();
                break;
            case "SUMMARISE" :
                setTextData(request.getInput().toString());
                aiResponse = new SummariserResponse();
                break;
            case "INS" :
                setTextData(request.getInput().toString());
                aiResponse = new InsightsResponse();
                break;
        }
    }

    public String getImgData() {
        return imgData;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getTextData() {
        return textData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }

    public AiResponse getAiResponse() {
        return aiResponse;
    }
}
