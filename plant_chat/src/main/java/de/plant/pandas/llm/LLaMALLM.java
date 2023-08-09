package de.plant.pandas.llm;

import com.google.gson.JsonObject;
import de.plant.pandas.Keys;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LLaMALLM extends WebserviceLLM {

    public LLaMALLM(URL url) {
        super(url);
    }
}
