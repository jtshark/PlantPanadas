package de.plant.pandas.llm;

import com.google.gson.JsonObject;
import de.plant.pandas.Keys;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class OpenAILLM extends WebserviceLLM {


    public enum OpenAIType {
        CHATGPT, GPT3
    }

    private final String openAIKey;

    public OpenAILLM(String openAIKey, OpenAIType openAIType) {
        super(switch (openAIType) {
            case CHATGPT -> {
                try {
                    yield new URL("https://api.openai.com/v1/chat/completions");
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            case GPT3 -> {
                try {
                    yield new URL("https://api.openai.com/v1/completions");
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.openAIKey = openAIKey;
    }

    public OpenAILLM(OpenAIType openAIType) {
        this(Keys.getProperty("OPENAI_KEY"), openAIType);
    }

    public OpenAILLM() {
        this(Keys.getProperty("OPENAI_KEY"), OpenAIType.CHATGPT);
    }


    @Override
    public void addExtraRequestMap(JsonObject json) {
        json.addProperty("model", "gpt-4");
    }

    @Override
    public void addRequestProperties(HttpURLConnection connection) {
        connection.setRequestProperty("Authorization", "Bearer " + openAIKey);
    }


}
