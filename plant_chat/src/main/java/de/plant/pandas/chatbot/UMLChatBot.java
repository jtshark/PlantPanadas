package de.plant.pandas.chatbot;

import java.util.List;
import java.util.Map;

public interface UMLChatBot {
    public Map<String,String> askQuestion(List<String> plantUMLs, String task);
}
