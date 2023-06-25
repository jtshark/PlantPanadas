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

        builder.append("Imagine three UML Experts verbally discussing how they design an UML diagram fulfilling the request from the client.\n");


        if (plantUMLs.isEmpty()) {
            builder.append("Currently there are no UML diagrams created.\n");
        } else {
            builder.append("The current UML can be described with PlantUML as:\n");

            for (String uml : plantUMLs) {
                builder.append(uml);
                builder.append("\n");
            }
        }

        builder.append("If something is unclear for the Experts they have to ask questions to the client.\n");
        builder.append("If one of them wants to ask a question back to the client they have to start the question with \"QUESTION:\" like so:\n");
        builder.append("QUESTION: <question> <EOQ>\n");

        messages.add(0, new Message(builder.toString(), MessageRole.SYSTEM));
        String output = llm.prompt(messages);
        if (output.contains("QUESTION:")) {
            messages.remove(0);
            String[] questions = output.split("QUESTION: ");
            String question = questions[questions.length - 1];
            return new UMLChatBotResults.ChatBotQuestions(question);
        } else {
            messages.add(new Message(output, MessageRole.ASSISTANT));
            messages.add(new Message("The UML Experts should now come up with a final step by step solution what they want to do.\n", MessageRole.SYSTEM));
            String steps = llm.prompt(messages);
            messages.add(new Message(steps, MessageRole.ASSISTANT));

            StringBuilder plantUMLInput = new StringBuilder();
            plantUMLInput.append("Create a PlantUML out of it.\n");
            plantUMLInput.append("All PlantUML outputs should a meaningful filename.\n");
            plantUMLInput.append("Do not change existing filenames.\n");
            plantUMLInput.append("But you are allowed to change file content, but then the filename must be the same.\n");
            plantUMLInput.append("<FILENAME>.puml\n");
            plantUMLInput.append("@startuml\n");
            plantUMLInput.append("...");
            plantUMLInput.append("@enduml\n");

            messages.add(new Message(plantUMLInput.toString(), MessageRole.SYSTEM));

            String plantUML = llm.prompt(messages);
            plantUML = plantUML.replaceAll("```", "");
            return new UMLChatBotResults.GeneratedUML(umlStringToMap(plantUML));
        }

    }
}
