package de.plant.pandas.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public abstract class WebserviceLLM implements LLM {
    private final URL llmURL;

    private final Gson gson = new Gson();

    protected WebserviceLLM(URL llmURL) {
        this.llmURL = llmURL;
    }

    public void addExtraRequestMap(JsonObject json) {
    }

    private String getRequestBody(List<Message> input, List<String> stopTokens, int maxTokens) {
        JsonObject json = new JsonObject();

        addExtraRequestMap(json);
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

        json.addProperty("max_tokens", maxTokens);
        json.addProperty("temperature", 0.4);

        if (!stopTokens.isEmpty()) {
            JsonArray stopTokensGson = new JsonArray();
            stopTokens.forEach(stopTokensGson::add);
            json.add("stop", stopTokensGson);
        }


        return json.toString();
    }

    public void addRequestProperties(HttpURLConnection connection) {
    }

    HttpURLConnection openConnectionToOpenAI() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) llmURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        addRequestProperties(connection);
        connection.setDoOutput(true);

        return connection;
    }

    void sendTextToWebservice(String body, HttpURLConnection connection) throws IOException {
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


        return (String) ((Map) choicesMap.get("message")).get("content");
    }

    @Override
    public String prompt(List<Message> input, List<String> stopTokens, int tokenLimit) {
        try {
            /*for (Message message : input) {
                System.out.print(switch (message.getMessageType()) {
                    case SYSTEM -> "System: ";
                    case HUMAN -> "Human: ";
                    case ASSISTANT -> "Assistant: ";
                });
                //System.out.println(message.getContent());
                //System.out.println();
            }*/

            String answer = null;
            int errorCounter = 0;
            while (answer == null) {
                HttpURLConnection openAIConnection = openConnectionToOpenAI();
                String query = getRequestBody(input, stopTokens, tokenLimit);
                sendTextToWebservice(query, openAIConnection);
                if (openAIConnection.getResponseCode() == 503) {
                    errorCounter++;
                    if (errorCounter == 3) {
                        throw new RuntimeException("Too many errors when connecting to Server");
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }

                } else {
                    String openAIResponse = readInputFromOpenAI(openAIConnection);
                    openAIConnection.disconnect();
                    answer = parseTextFromOpenAI(openAIResponse);
                }
            }
            return answer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}