package de.plant.pandas.plantuml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseParserImpl implements ResponseParser {
    @Override
    public Map<String, String> umlStringToMap(String umlString) {
        umlString = umlString.replaceAll("```plantuml", "");
        umlString = umlString.replaceAll("```puml", "");
        umlString = umlString.replaceAll("```", "");

        System.out.println(umlString);
        Map<String, String> umlMap = new HashMap<>();

        // Regex pattern to match filename and corresponding UML content
        Pattern p = Pattern.compile("\"*(\\w+\\.puml)\"*:*\\n*(@startuml\\n[\\s\\S]*?@enduml)", Pattern.DOTALL);
        Matcher m = p.matcher(umlString);

        while (m.find()) {
            String filename = m.group(1);
            String content = m.group(2);
            umlMap.put(filename, content);
        }

        return umlMap;
    }

    @Override
    public String getQuestion(String output) {
        String[] questions = output.split("QUESTION: ");
        String question = questions[questions.length - 1];
        return question;
    }
}
