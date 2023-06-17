package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;

import java.util.*;
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

        builder.append("Client: ").append(task).append("\n");
        builder.append("Imagine three UML Experts discussing how they design an UML diagram fulfilling the request from the client.\n");


        if (plantUMLs.isEmpty()) {
            builder.append("Currently there are no UML diagrams created.\n");
        } else {
            builder.append("The current UML can be described with PlantUML as:\n");

            for (String uml : plantUMLs) {
                builder.append(uml);
                builder.append("\n");
            }
        }

        builder.append("If something is unclear for the Experts they can ask questions to the client.\n");
        builder.append("If one of them wants to ask a question they must ask with:\n");
        builder.append("QUESTION: <question> <EOQ>\n");

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(builder.toString(), MessageRole.HUMAN));

        String output = llm.prompt(messages);
        System.out.println(output);

        return umlStringToMap(output);

    }
}
