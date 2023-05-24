package de.plant.pandas.llm;

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
    private final Gson gson = new Gson();


    private String getRequestBody(String input) {
        JsonObject json = new JsonObject();

        json.addProperty("model", "text-davinci-003");
        json.addProperty("prompt", input);
        json.addProperty("max_tokens", 200);
        json.addProperty("temperature", 0);

        return json.toString();
    }


    HttpURLConnection openConnectionToOpenAI() throws IOException {
        URL url = new URL("https://api.openai.com/v1/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + Keys.getProperty("OPENAI_KEY"));
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
        return (String) choicesMap.get("text");
    }

    @Override
    public String prompt(String input) {
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
