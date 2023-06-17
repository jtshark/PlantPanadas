package de.plant.pandas;

import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.llm.LLM;

import java.util.Collections;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        LLM llm = new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT);

        UMLChatBot umlChatBot = new UMLChatBotImpl(llm);
        Map<String, String> umlDiagram = umlChatBot.askQuestion(Collections.emptyList(), "42");
        System.out.println(umlDiagram);
    }
}