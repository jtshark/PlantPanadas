package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UMLChatBotImpl implements UMLChatBot {
    private final LLM llm;

    public UMLChatBotImpl(LLM llm) {
        this.llm = llm;
    }

    private Map<String, String> umlStringToMap(String umlString) {
        Map<String, String> umlMap = new HashMap<>();

        // Regex pattern to match filename and corresponding UML content
        Pattern p = Pattern.compile("(\\w+\\.puml)\\n*(@startuml\\n[\\s\\S]*?@enduml)", Pattern.DOTALL);
        Matcher m = p.matcher(umlString);

        while (m.find()) {
            String filename = m.group(1);
            String content = m.group(2);
            umlMap.put(filename, content);
        }

        return umlMap;
    }

    public Map<String, String> askQuestion(Collection<String> plantUMLs, String task) {
        StringBuilder builder = new StringBuilder();

        builder.append("You are an assistant for helping people with UML class diagrams.\n");
        builder.append("You are controlled through the PlantUML language.\n");

        if (plantUMLs.isEmpty()) {
            builder.append("Currently there are no UML diagrams created.\n");
        } else {
            builder.append("The current UML can be described with PlantUML as:\n");

            for (String uml : plantUMLs) {
                builder.append(uml);
                builder.append("\n");
            }
            builder.append("output existing diagrams only, if there have been any changes.\n");
        }

        builder.append("Your task is:\n");
        builder.append(task);
        builder.append("\n");
        builder.append("You should output your answer as PlantUML and you should only output PlantUML commands.\n");
        builder.append("Keep your answer as short as possible and only insert things the user asked for.\n");
        builder.append("Start your output with a meaningful filename.\n");
        builder.append("Do not change existing filenames.\n");
        builder.append("But you are allowed to change file content, but then the filename must be the same.\n");
        builder.append("<FILENAME>.puml\n");
        builder.append("@startuml\n");
        builder.append("...");
        builder.append("@enduml\n");

        String output = llm.prompt(builder.toString());


        return umlStringToMap(output);

    }
}
