package de.plant.pandas.llm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.plant.pandas.Keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


public class OpenAILLM implements LLM {


    public enum OpenAIType {
        CHATGPT, GPT3
    }

    private final Gson gson = new Gson();
    private final String openAIKey;
    private final OpenAIType openAIType;

    public OpenAILLM(String openAIKey, OpenAIType openAIType) {
        this.openAIKey = openAIKey;
        this.openAIType = openAIType;
    }

    public OpenAILLM(OpenAIType openAIType) {
        this(Keys.getProperty("OPENAI_KEY"), openAIType);
    }

    public OpenAILLM() {
        this(Keys.getProperty("OPENAI_KEY"), OpenAIType.CHATGPT);
    }


    private String getRequestBody(List<Message> input) {
        JsonObject json = new JsonObject();

        String model = switch (openAIType) {
            case CHATGPT -> "gpt-3.5-turbo-16k";
            case GPT3 -> "text-davinci-003";
        };
        json.addProperty("model", model);


        if (openAIType == OpenAIType.CHATGPT) {
            JsonArray messages = new JsonArray();

            for (Message m : input) {
                JsonObject message = new JsonObject();
                message.addProperty("role", switch (m.getMessageType()) {
                    case HUMAN -> "user";
                    case ASSISTANT -> "assistant";
                    case SYSTEM -> "system";
                });
                message.addProperty("content", m.getContent());
                messages.add(message);
                json.add("messages", messages);
            }

        } else if (openAIType == OpenAIType.GPT3) {
            System.out.println("Extra for GPT3");
        }

        json.addProperty("max_tokens", 2000);
        json.addProperty("temperature", 0);

        JsonArray stopTokens = new JsonArray();
        stopTokens.add("<EOQ>");
        json.add("stop", stopTokens);

        return json.toString();
    }


    HttpURLConnection openConnectionToOpenAI() throws IOException {

        URL url = switch (openAIType) {
            case CHATGPT -> new URL("https://api.openai.com/v1/chat/completions");
            case GPT3 -> new URL("https://api.openai.com/v1/completions");
        };

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + openAIKey);
        connection.setDoOutput(true);

        return connection;
    }

    void sendTextToOpenAI(String body, HttpURLConnection connection) throws IOException {
        OutputStream os = connection.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        os.close();
    }

    String readInputFromOpenAI(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();


        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    String parseTextFromOpenAI(String openAIResponse) {
        Map data = gson.fromJson(String.valueOf(openAIResponse), Map.class);
        List choices = (List) data.get("choices");
        Map choicesMap = (Map) choices.get(0);

        return switch (openAIType) {
            case CHATGPT -> (String) ((Map) choicesMap.get("message")).get("content");
            case GPT3 -> (String) choicesMap.get("text");
        };
    }

    @Override
    public String prompt(List<Message> input) {
        try {

            HttpURLConnection openAIConnection = openConnectionToOpenAI();
            String query = getRequestBody(input);
            sendTextToOpenAI(query, openAIConnection);
            String openAIResponse = readInputFromOpenAI(openAIConnection);
            openAIConnection.disconnect();
            return parseTextFromOpenAI(openAIResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
