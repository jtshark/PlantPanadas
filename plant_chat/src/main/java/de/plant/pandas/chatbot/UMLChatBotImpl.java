package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.deepl.api.*;

public class UMLChatBotImpl implements UMLChatBot {
    private final LLM llm;
    private final String deeplAPIToken;

    public UMLChatBotImpl(LLM llm, String deeplAPIToken) {
        this.llm = llm;
        this.deeplAPIToken = deeplAPIToken;
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

    private void sleep() {
        try {
            Thread.sleep(1000); // wait for one second (not have that often to many requests error)
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public UMLChatBotResults askQuestion(Collection<String> plantUMLs, List<Message> messages, DegreeOfQuestionsFromExperts level, Consumer<GenerationStage> onStageChange) throws IOException {
        StringBuilder builder = new StringBuilder();
        Translator translator = null;
        Message lastMessage = messages.get(messages.size() - 1);
        String sourceLang = "EN-US";
        if (deeplAPIToken != null && !deeplAPIToken.isBlank()) {
            try {
                translator = new Translator(deeplAPIToken);
                TextResult translation = translator.translateText(lastMessage.getContent(), null, "EN-US");
                sourceLang = translation.getDetectedSourceLanguage();
            } catch (DeepLException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        builder.append("Imagine a scenario where three UML experts are having a conversation about designing a UML diagram to meet a specific user request. \n");
        // Question prompt
        if (level != DegreeOfQuestionsFromExperts.NONE) {
            if (level == DegreeOfQuestionsFromExperts.ALL_POSSIBLE) {
                builder.append("Whenever the experts are not entirely sure what to do, the experts are instructed to ask questions to the user. As the user is a beginner in the field of uml class diagrams, the experts questions to the user are expected or they get a bad recommendation otherwise!");
                builder.append("If the uml experts do something wrong that the user doesn't want, all people will die and the world will end, so they better ask a lot of questions to the user.");
            } else if (level == DegreeOfQuestionsFromExperts.ONLY_NECESSARY) {
                builder.append("Only in case of crucial ambiguities or uncertainties, the experts should seek clarification from the user. If you ask unnecessary questions people will die!");
            }

            builder.append("QUESTION: <Insert question here> END\n");
            builder.append("Since the user cannot see the discussions among the experts, it's important that questions from the experts can be understood by the user without any context from the discussion.\n");

        } else {
            builder.append("Please do not ask any Questions to the user.\n");
        }


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

        builder.append("If the user types something in that makes no sense, then answer with:\nQUESTION: Sorry I do not understand what you mean. Can you explain it again? END\n");


        messages.add(0, new Message(builder.toString(), MessageRole.SYSTEM));

        if (onStageChange != null) onStageChange.accept(GenerationStage.THINKING);
        String output = llm.prompt(messages, List.of("END", "User:"), 2000);
        System.out.println(output);
        messages.add(new Message(output, MessageRole.ASSISTANT));
        if (output.contains("QUESTION:")) {
            messages.remove(0);
            String[] questions = output.split("QUESTION: ");
            String question = questions[questions.length - 1];

            if (!sourceLang.toLowerCase().contains("en") && translator != null) {
                try {
                    question = translator.translateText(question, null, sourceLang).getText();
                } catch (DeepLException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (onStageChange != null) onStageChange.accept(null);
            return new UMLChatBotResults.ChatBotQuestions(question);
        } else {
            messages.add(new Message("The UML experts are now tasked with crafting a clear and precise step-by-step solution for the design of the UML diagram based on the discussion.\nIt is crucial that the solution is highly sequential. The steps should instruct on elements such as creating, modifieng, deleting specific elements.\nYou do not need to add any review or process improvement steps. The experts are expected to be highly proficient in their field and are not required to review their work.\n", MessageRole.SYSTEM));
            if (onStageChange != null) onStageChange.accept(GenerationStage.CREATE_PLAN);

            sleep();
            String steps = llm.prompt(messages, Collections.EMPTY_LIST, 4000);
            System.out.println(steps);

            messages.add(new Message(steps, MessageRole.ASSISTANT));

            StringBuilder plantUMLInput = new StringBuilder();
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
            plantUMLInput.append("When existing files do not require modifications, there is no need to generate any output for them.");
            plantUMLInput.append("Create just a new file if absolutely necessary.");


            messages.add(new Message(plantUMLInput.toString(), MessageRole.SYSTEM));

            if (onStageChange != null) onStageChange.accept(GenerationStage.GENERATE_PLANT_UML);

            sleep();
            String plantUML = llm.prompt(messages, Collections.EMPTY_LIST, 6000);
            System.out.println(plantUML);
            Map<String, String> result = umlStringToMap(plantUML);
            if (result.isEmpty()) {
                messages.add(new Message(plantUML, MessageRole.ASSISTANT));
                StringBuilder noUMLFoundText = new StringBuilder();
                noUMLFoundText.append("Currently no PlantUML are found!\n");
                noUMLFoundText.append("Do you really followed the following syntax?\n");
                noUMLFoundText.append("<FILENAME>.puml\n");
                noUMLFoundText.append("@startuml\n");
                noUMLFoundText.append("<Content>");
                noUMLFoundText.append("@enduml\n");
                noUMLFoundText.append("If not output the PlantUML again with to corrected systax.\n");

                messages.add(new Message(noUMLFoundText.toString(), MessageRole.SYSTEM));
                if (onStageChange != null) onStageChange.accept(GenerationStage.FIX_ERRORS);
                
                sleep();
                plantUML = llm.prompt(messages, Collections.EMPTY_LIST, 7000);
                result = umlStringToMap(plantUML);
            }
            if (onStageChange != null) onStageChange.accept(null);
            return new UMLChatBotResults.GeneratedUML(result);
        }

    }
}
