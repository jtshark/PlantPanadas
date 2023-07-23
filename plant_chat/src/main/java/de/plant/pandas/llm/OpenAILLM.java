package de.plant.pandas.llm;

import com.google.gson.JsonObject;
import de.plant.pandas.Keys;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class OpenAILLM extends WebserviceLLM {


    public enum OpenAIType {
        CHATGPT, GPT4
    }

    private final String openAIKey;
    private final OpenAIType openAIType;

    public OpenAILLM(String openAIKey, OpenAIType openAIType) throws MalformedURLException {
        super(new URL("https://api.openai.com/v1/chat/completions"));
        this.openAIKey = openAIKey;
        this.openAIType = openAIType;
    }

    public OpenAILLM(OpenAIType openAIType) throws MalformedURLException {
        this(Keys.getProperty("OPENAI_KEY"), openAIType);
    }

    public OpenAILLM() throws MalformedURLException {
        this(Keys.getProperty("OPENAI_KEY"), OpenAIType.CHATGPT);
    }


    @Override
    public void addExtraRequestMap(JsonObject json) {

        String type = switch (openAIType) {
            case CHATGPT -> "gpt-3.5-turbo-16k";
            case GPT4 -> "gpt-4";
        };

        json.addProperty("model", type);
    }

    @Override
    public void addRequestProperties(HttpURLConnection connection) {
        connection.setRequestProperty("Authorization", "Bearer " + openAIKey);
    }


}
