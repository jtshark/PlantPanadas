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

    public Map<String, String> umlStringToMap(String umlString) {
        umlString = umlString.replaceAll("```plantuml", "");
        umlString = umlString.replaceAll("```puml", "");
        umlString = umlString.replaceAll("```", "");

        System.out.println(umlString);
        Map<String, String> umlMap = new HashMap<>();

        // Regex pattern to match filename and corresponding UML content
        Pattern p = Pattern.compile("(\\w+\\.puml:*)\\n*(@startuml\\n[\\s\\S]*?@enduml)", Pattern.DOTALL);
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

        builder.append("Envision a scenario where three UML experts are having a conversation about designing a UML diagram to meet a specific user request. \n");
        builder.append("In case of any ambiguities or uncertainties, the experts may need to seek clarification from the user. If such a situation arises, they should preface their question to the user with the statement \"QUESTION:\" and conclude it with \"END\", as in the following example:\n");
        builder.append("QUESTION: <Insert question here> END\n");
        builder.append("Please note, the user is not included in the generated conversation and should not be portrayed in the dialogue. Any inquiries made by the experts are to be formulated strictly following the above format.\n");
        builder.append("They should explore and discuss practical real-world scenarios. For example, they could delve into why it's often more logical to save a date of birth attribute and a method getAge rather than directly storing the age.\n");
        builder.append("Or when instead of saving a price and a discounted price it is often better to save a price and a discount and a method getDiscountedPrice.\n");
        builder.append("Encourage them to illustrate these concepts using concrete examples and clear logic.\n");


        if (plantUMLs.isEmpty()) {
            builder.append("Currently there are no UML diagrams created.\n");
        } else {
            builder.append("The current UML can be described with PlantUML as:\n");

            for (String uml : plantUMLs) {
                builder.append(uml);
                builder.append("\n");
            }
        }

        messages.add(0, new Message(builder.toString(), MessageRole.SYSTEM));
        String output = llm.prompt(messages, List.of("END", "User:"), 3000);
        System.out.println(output);
        messages.add(new Message(output, MessageRole.ASSISTANT));
        if (output.contains("QUESTION:")) {
            messages.remove(0);
            String[] questions = output.split("QUESTION: ");
            String question = questions[questions.length - 1];
            return new UMLChatBotResults.ChatBotQuestions(question);
        } else {
            messages.add(new Message("The UML experts are now tasked with crafting a clear and precise step-by-step solution for the design of the UML diagramm based on the discussion.\nIt is crucial that the solution is highly sequential. The steps should instruct on elements such as creating, modifieng, deleting specific elements.\nYou do not need to add any review or process improvement steps. The experts are expected to be highly proficient in their field and are not required to review their work.\n", MessageRole.SYSTEM));
            String steps = llm.prompt(messages, Collections.EMPTY_LIST, 8000);
            System.out.println(steps);

            messages.add(new Message(steps, MessageRole.ASSISTANT));

            StringBuilder plantUMLInput = new StringBuilder();
            /*plantUMLInput.append("Create a PlantUML out of it.\n");
            plantUMLInput.append("All PlantUML outputs should a meaningful filename.\n");
            plantUMLInput.append("Do not change existing filenames.\n");
            plantUMLInput.append("But you are allowed to change file content, but then the filename must be the same.\n");
            plantUMLInput.append("Create only new files if it is necessary and put related objects in the same file.\n");
            plantUMLInput.append("<FILENAME>.puml\n");
            plantUMLInput.append("@startuml\n");
            plantUMLInput.append("...");
            plantUMLInput.append("@enduml\n");*/
            plantUMLInput.append("Design a PlantUML with the following guidelines based on the step by step guide:\n");
            plantUMLInput.append("1. Each PlantUML output must have a meaningful filename.\n");
            plantUMLInput.append("2. Existing filenames should not be altered. However, the content of the file can be changed as necessary. When changes are made, ensure the filename remains the same.");
            plantUMLInput.append("3. Only create new files if absolutely necessary. Whenever possible, keep related objects within the same file.\n");
            plantUMLInput.append("Here's the required syntax for the PlantUML files:\n");
            plantUMLInput.append("<FILENAME>.puml\n");
            plantUMLInput.append("@startuml\n");
            plantUMLInput.append("<Content>");
            plantUMLInput.append("@enduml\n");
            plantUMLInput.append("Please note: It's crucial to maintain this exact syntax for the PlantUML files, and not alter it in any way.\n");
            plantUMLInput.append("When existing files do not require modifications, there is no need to generate any output for them.p");

            messages.add(new Message(plantUMLInput.toString(), MessageRole.SYSTEM));

            String plantUML = llm.prompt(messages, Collections.EMPTY_LIST, 14000);
            System.out.println(plantUML);
            return new UMLChatBotResults.GeneratedUML(umlStringToMap(plantUML));
        }

    }
}
