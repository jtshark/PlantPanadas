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

    public UMLChatBotResults askQuestion(Collection<String> plantUMLs, List<Message> messages) {
        StringBuilder builder = new StringBuilder();

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
        builder.append("If one of them wants to ask a question they must definitely in every situation ask with:\n");
        builder.append("QUESTION: <question> <EOQ>\n");

        messages.add(0, new Message(builder.toString(), MessageRole.SYSTEM));

        String output = llm.prompt(messages);
        messages.remove(0);
        if (output.contains("QUESTION:")) {
            String[] questions = output.split("QUESTION: ");
            String question = questions[questions.length - 1];
            return new UMLChatBotResults.ChatBotQuestions(question);
        } else {
            System.out.println(output);
            return new UMLChatBotResults.GeneratedUML(umlStringToMap(output));
        }

    }
}
