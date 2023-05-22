package de.plant.pandas;

import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.llm.LLM;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        LLM llm = new OpenAILLM();

        UMLChatBot umlChatBot = new UMLChatBot(llm);
        String umlDiagram = umlChatBot.askQuestion(Collections.emptyList(),"I want a Pizzashop.");
        System.out.println(umlDiagram);
    }
}