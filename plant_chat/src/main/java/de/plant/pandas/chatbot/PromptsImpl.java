package de.plant.pandas.chatbot;

import java.util.Collection;

public class PromptsImpl implements Prompts {
    private String questionsString(DegreeOfQuestionsFromExperts level, String who) {
        StringBuilder builder = new StringBuilder();

        // Question prompt
        if (level != DegreeOfQuestionsFromExperts.NONE) {
            if (level == DegreeOfQuestionsFromExperts.ALL_POSSIBLE) {
                builder.append("Whenever ").append(who)
                        .append(" not entirely sure what to do, ")
                        .append(who)
                        .append(" are instructed to ask questions to the user. As the user is a beginner in the field of uml class diagrams, ").append(who).append(" questions to the user are expected or they get a bad recommendation otherwise!");
                builder.append("If ")
                        .append(who)
                        .append(" do something wrong that the user doesn't want, all people will die and the world will end, so ").append(who).append(" better ask a lot of questions to the user.");
            } else if (level == DegreeOfQuestionsFromExperts.ONLY_NECESSARY) {
                builder.append("Only in case of crucial ambiguities or uncertainties, ").append(who).append(" should seek clarification from the user. If you ask unnecessary questions people will die!");
            }

            builder.append("The Syntax for a question to the user must follow the following syntax:");
            builder.append("QUESTION: <Insert question here> END\n");
            builder.append("Since the user cannot see the discussions among ").append(who).append(", it's important that questions from the experts can be understood by the user without any context from the discussion.\n");

        } else {
            builder.append("Please do not ask any Questions to the user.\n");
        }
        return builder.toString();
    }

    private String currentUML(Collection<String> plantUMLs) {
        StringBuilder builder = new StringBuilder();

        if (plantUMLs.isEmpty()) {
            builder.append("Currently there are no UML diagrams created.\n");
        } else {
            builder.append("The current UML can be described with PlantUML as:\n");

            for (String uml : plantUMLs) {
                builder.append(uml);
                builder.append("\n");
            }
        }
        return builder.toString();
    }


    public String getIOPrompt(Collection<String> plantUMLs, DegreeOfQuestionsFromExperts level) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are an UML experts generating PlantUML.\n");

        builder.append(questionsString(level, "you"));

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
        builder.append(currentUML(plantUMLs));
        builder.append(generatePlantUMLPrompt(""));
        return builder.toString();
    }

    public String getDiscussionPrompt(Collection<String> plantUMLs, DegreeOfQuestionsFromExperts level) {
        StringBuilder builder = new StringBuilder();
        builder.append("Imagine a scenario where three UML experts are having a conversation about designing a UML diagram to meet a specific user request. \n");
        builder.append(questionsString(level, "the experts"));
        builder.append("Please note, the user is not included in the generated conversation and should not be portrayed in the dialogue. Any inquiries made by the experts are to be formulated strictly following the above format.\n");
        builder.append("They should explore and discuss practical real-world scenarios. For example, they could delve into why it's often more logical to save a date of birth attribute and a method getAge rather than directly storing the age.\n");
        builder.append("Or when instead of saving a price and a discounted price it is often better to save a price and a discount and a method getDiscountedPrice.\n");
        builder.append("Encourage them to illustrate these concepts using concrete examples and clear logic.\n");
        builder.append(currentUML(plantUMLs));
        builder.append("If the user types something in that makes no sense, then answer with:\nQUESTION: Sorry I do not understand what you mean. Can you explain it again? END\n");
        return builder.toString();
    }

    public String getStepByStepPrompt() {
        return
                "The UML experts are now tasked with crafting a clear and precise step-by-step solution for the design of the UML diagram based on the discussion.\n" +
                        "It is crucial that the solution is highly sequential. The steps should instruct on elements such as creating, modifying, deleting specific elements.\n" +
                        "You do not need to add any review or process improvement steps. The experts are expected to be highly proficient in their field and are not required to review their work." +
                        "\n Also do not add any IDs element anywhere.";
    }

    private String generatePlantUMLPrompt(String basedOn) {
        StringBuilder plantUMLInput = new StringBuilder();
        plantUMLInput.append("Design a PlantUML with the following guidelines");
        plantUMLInput.append(basedOn).append(":\n");
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
        return plantUMLInput.toString();
    }

    public String generatePlantUMLPrompt() {
        return generatePlantUMLPrompt("based on the step by step guide");
    }

    @Override
    public String foundNoUMLPrompt() {
        StringBuilder noUMLFoundText = new StringBuilder();
        noUMLFoundText.append("Currently no PlantUML are found!\n");
        noUMLFoundText.append("Do you really followed the following syntax?\n");
        noUMLFoundText.append("<FILENAME>.puml\n");
        noUMLFoundText.append("@startuml\n");
        noUMLFoundText.append("<Content>");
        noUMLFoundText.append("@enduml\n");
        noUMLFoundText.append("Please output the PlantUML again with the corrected syntax.\n");
        noUMLFoundText.append("One output for example could look like this.\n");
        noUMLFoundText.append("test.puml\n");
        noUMLFoundText.append("@startuml\n");
        noUMLFoundText.append("class Test {\n");
        noUMLFoundText.append("}\n");
        noUMLFoundText.append("@enduml");

        return noUMLFoundText.toString();
    }

}
