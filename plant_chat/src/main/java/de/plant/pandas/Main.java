package de.plant.pandas;

import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.llm.LLM;

import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        LLM llm = new OpenAILLM();

        UMLChatBot umlChatBot = new UMLChatBotImpl(llm);
        String umlDiagram = umlChatBot.askQuestion(Collections.emptyList(), "Can you create me a Pizzashop and a seperate Iceshop?.");
        System.out.println(umlDiagram);
    }
}