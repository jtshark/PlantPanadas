package de.plant.pandas.llm;

import de.plant.pandas.Keys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


public class OpenAILLM implements LLM{
    private final Gson gson = new Gson();

    @Override
    public String prompt(String input) {
        try {
            URL url = new URL("https://api.openai.com/v1/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer "+ Keys.getProperty("OPENAI_KEY"));
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            String query = "{ " +
                    "\"model\": \"text-davinci-003\", " +
                    "\"prompt\":\"" + input+ "\", " +
                    "\"max_tokens\": 200, " +
                    "\"temperature\": 0" +
                    "}";
            os.write(query.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();


            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            Map data = gson.fromJson(String.valueOf(content), Map.class);
            List choices = (List) data.get("choices");
            Map choicesMap= (Map) choices.get(0);
            String output = (String) choicesMap.get("text");
            in.close();
            connection.disconnect();

            System.out.println("Response: " + output.trim());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "You got rick-rolled!";
    }
}
