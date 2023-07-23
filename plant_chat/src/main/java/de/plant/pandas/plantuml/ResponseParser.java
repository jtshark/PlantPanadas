package de.plant.pandas.plantuml;

import java.util.Map;

public interface ResponseParser {
    Map<String, String> umlStringToMap(String umlString);

    String getQuestion(String umlString);

}
