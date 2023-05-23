package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;

import java.util.List;

public class UMLChatBotImpl implements UMLChatBot
{
   private final LLM llm;

    public UMLChatBotImpl(LLM llm) {
        this.llm = llm;
    }

    public String askQuestion(List<String> plantUMLs, String task)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("You are an assistant for helping people with UML class diagrams.\n");
        builder.append("You are controlled through the PlantUML language\n");

        if(plantUMLs.isEmpty())
        {
            builder.append("Currently there are no UML diagrams created.\n");
        }
        else {
            builder.append("The current UML can be described with PlantUML as:\n");

            for (String uml:plantUMLs) {
                builder.append(uml);
                builder.append("\n");
            }
        }

        builder.append("You Task is:\n");
        builder.append(task);
        builder.append("\n");
        builder.append("You should output your answer as PlantUML and you should only output PlantUML commands.\n");

        return llm.prompt(builder.toString());

    }
}
